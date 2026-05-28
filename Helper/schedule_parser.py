from __future__ import annotations

import os
import re
import tempfile
from typing import Literal

import pdfplumber
from fastapi import FastAPI, File, HTTPException, UploadFile
from pydantic import BaseModel
from dotenv import load_dotenv


app = FastAPI()

# uvicorn schedule_parser:app --host 0.0.0.0 --port 9001 --reload

load_dotenv()

class Settings:
    PORT = int(os.getenv("PORT", 9001))
    STORAGE_ENDPOINT = os.getenv("STORAGE_ENDPOINT")
    STORAGE_ACCESS_KEY = os.getenv("STORAGE_ACCESS_KEY")
    STORAGE_SECRET_KEY = os.getenv("STORAGE_SECRET_KEY")
    STORAGE_BUCKET = os.getenv("STORAGE_BUCKET_NAME", "schedule")

settings = Settings()


WeekType = Literal["ALL", "NUMERATOR", "DENOMINATOR"]
 

class LessonEntry(BaseModel):
    day: str
    pair_number: int
    pair_label: str
    time_start: str
    time_end: str
    week_type: WeekType
    groups: list[str]
    subject: str | None = None
    lesson_type: str | None = None
    room: str | None = None
    teachers: list[str] = []
    raw_text: str


class ScheduleDocument(BaseModel):
    source_file: str
    semester: str | None = None
    academic_year: str | None = None
    groups: list[str]
    entries: list[LessonEntry]


LESSON_TYPE_MAP = {
    "лекція": "LECTURE",
    "лаб": "LAB",
    "лаб.": "LAB",
    "лаб,": "LAB",
    "практ": "PRACTICAL",
    "практ.": "PRACTICAL",
    "практ,": "PRACTICAL",
}

DAY_ALIASES = {
    "понеділок": "Понеділок",
    "вівторок": "Вівторок",
    "середа": "Середа",
    "четвер": "Четвер",
    "пятниця": "П'ятниця",  # Ключ без апострофа, значення - правильне
    "субота": "Субота",
    "неділя": "Неділя",
}

ROMAN_TO_NUMBER = {
    "І": 1,
    "ІІ": 2,
    "II": 2,
    "ІІІ": 3,
    "III": 3,
    "ІV": 4,
    "IV": 4,
    "V": 5,
    "VI": 6,
    "VІ": 6,
}

TEACHER_PATTERN = re.compile(
    r"(?:доц\.|проф\.|ас\.|ст\. ?в\.|ст\. ?викл\.)\s*"
    r"[А-ЯІЇЄҐ][а-яіїєґ'’\-]+(?:\s+[А-ЯІЇЄҐ]\.[А-ЯІЇЄҐ]\.)"
)


def normalize_spaces(text: str) -> str:
    return re.sub(r"[ \t]+", " ", text.replace("\xa0", " ")).strip()


def clean_multiline_text(text: str) -> str:
    lines = [normalize_spaces(line) for line in text.splitlines()]
    lines = [line for line in lines if line]
    return "\n".join(lines).strip()


def normalize_day(raw: str | None) -> str | None:
    if not raw:
        return None

    # Залишаємо виключно українські літери у нижньому регістрі.
    # Це автоматично "вб'є" всі пробіли, апострофи, цифри, переноси та невидимі символи.
    txt = re.sub(r"[^а-яіїєґ]", "", raw.lower())
    
    # Тепер рядок гарантовано чистий (наприклад, "яцинтяп"). 
    # Робимо 2 варіанти: прямий і перевернутий
    candidates = [txt, txt[::-1]]

    for candidate in candidates:
        for key, value in DAY_ALIASES.items():
            if key in candidate:
                return value

    return raw.strip()


def parse_pair_label(raw: str) -> tuple[str, int, str, str]:
    txt = normalize_spaces(raw.replace("\n", " "))

    roman_match = re.search(r"(І{1,3}|ІV|IV|V|VI|VІ|II|III)", txt)
    time_match = re.search(r"(\d{1,2})\s*(\d{2})\s*[–-]\s*(\d{1,2})\s*(\d{2})", txt)

    pair_label = roman_match.group(1) if roman_match else txt
    pair_number = ROMAN_TO_NUMBER.get(pair_label, 0)

    time_start = ""
    time_end = ""
    if time_match:
        time_start = f"{int(time_match.group(1)):02d}:{time_match.group(2)}"
        time_end = f"{int(time_match.group(3)):02d}:{time_match.group(4)}"

    return pair_label, pair_number, time_start, time_end


def extract_lines(page: pdfplumber.page.Page) -> tuple[list[dict], list[dict]]:
    hlines: list[dict] = []
    vlines: list[dict] = []

    for rect in page.rects:
        width = float(rect["width"])
        height = float(rect["height"])

        if width >= 10 and height <= 2.5:
            hlines.append(
                {
                    "x0": float(rect["x0"]),
                    "x1": float(rect["x1"]),
                    "y": (float(rect["top"]) + float(rect["bottom"])) / 2,
                }
            )

        if height >= 10 and width <= 2.5:
            vlines.append(
                {
                    "x": (float(rect["x0"]) + float(rect["x1"])) / 2,
                    "y0": float(rect["top"]),
                    "y1": float(rect["bottom"]),
                }
            )

    return hlines, vlines


def merged_coverage(segments: list[tuple[float, float]], gap_tol: float = 1.5) -> float:
    if not segments:
        return 0.0

    segments = sorted(segments)
    merged = [[segments[0][0], segments[0][1]]]

    for start, end in segments[1:]:
        if start <= merged[-1][1] + gap_tol:
            merged[-1][1] = max(merged[-1][1], end)
        else:
            merged.append([start, end])

    return sum(end - start for start, end in merged)


def vertical_boundary_exists(
    vlines: list[dict],
    x: float,
    y0: float,
    y1: float,
    tol: float = 2.0,
    min_coverage_ratio: float = 0.45,
) -> bool:
    segments: list[tuple[float, float]] = []

    for line in vlines:
        if abs(line["x"] - x) <= tol:
            start = max(y0, line["y0"])
            end = min(y1, line["y1"])
            if end > start:
                segments.append((start, end))

    return merged_coverage(segments) >= (y1 - y0) * min_coverage_ratio


def cluster_values(values: list[float], tol: float = 2.5) -> list[float]:
    if not values:
        return []

    values = sorted(values)
    clusters = [[values[0]]]

    for value in values[1:]:
        if abs(value - clusters[-1][-1]) <= tol:
            clusters[-1].append(value)
        else:
            clusters.append([value])

    return [sum(cluster) / len(cluster) for cluster in clusters]


def find_horizontal_splits(
    hlines: list[dict],
    x0: float,
    x1: float,
    y0: float,
    y1: float,
    margin: float = 5,
    min_coverage_ratio: float = 0.7,
) -> list[float]:
    target_width = x1 - x0
    candidates: list[float] = []

    for line in hlines:
        if not (y0 + margin < line["y"] < y1 - margin):
            continue

        overlap = max(0.0, min(x1, line["x1"]) - max(x0, line["x0"]))
        if overlap >= target_width * min_coverage_ratio:
            candidates.append(line["y"])

    return cluster_values(candidates)


def extract_region_text(page: pdfplumber.page.Page, bbox: tuple[float, float, float, float]) -> str:
    x0, y0, x1, y1 = bbox
    text = page.crop((x0 + 1, y0 + 1, x1 - 1, y1 - 1)).extract_text(
        x_tolerance=1,
        y_tolerance=2,
    ) or ""
    return clean_multiline_text(text)


def meaningful_text(text: str) -> bool:
    flat = normalize_spaces(text.replace("\n", " "))

    if len(flat) < 5:
        return False

    if re.search(r"(лекція|лаб\.?|практ\.?)", flat, flags=re.IGNORECASE):
        return True

    if TEACHER_PATTERN.search(flat):
        return True

    if re.search(r"\b\d{2,3}[а-яa-z]?(?:/\d{1,3})?(?:\([а-яa-z]\))?\b", flat, flags=re.IGNORECASE):
        return True

    return False


def parse_meta_from_page(page: pdfplumber.page.Page) -> tuple[str | None, str | None]:
    text = page.extract_text() or ""

    semester = None
    academic_year = None

    semester_match = re.search(r"на\s+([ІI]+)\s+семестр", text, flags=re.IGNORECASE)
    if semester_match:
        semester = semester_match.group(1)

    year_match = re.search(r"(\d{4}/\d{4})\s*н\.\s*р\.", text)
    if year_match:
        academic_year = year_match.group(1)

    return semester, academic_year


def extract_header_groups(page: pdfplumber.page.Page, table) -> list[dict]:
    groups: list[dict] = []
    header_cells = table.rows[0].cells

    for cell in header_cells[2:]:
        if cell is None:
            continue

        x0, y0, x1, y1 = cell
        text = extract_region_text(page, (x0, y0, x1, y1)).replace("\n", " ")
        text = normalize_spaces(text)

        if text:
            groups.append({"name": text, "x0": x0, "x1": x1})

    return groups


def parse_lesson_block(raw_text: str) -> dict | None:
    if not raw_text:
        return None

    raw_text = clean_multiline_text(raw_text)
    if not meaningful_text(raw_text):
        return None

    flat = normalize_spaces(raw_text.replace("\n", " "))

    type_match = re.search(
        r"\b(лекція|лаб\.?|лаб,|практ\.?|практ,|практ)\b",
        flat,
        flags=re.IGNORECASE,
    )

    subject = None
    lesson_type = None
    room = None

    if type_match:
        subject = normalize_spaces(flat[: type_match.start()].strip(" ,.-"))
        type_token = type_match.group(1).lower()
        lesson_type = LESSON_TYPE_MAP.get(type_token, type_token.upper())
        rest = flat[type_match.end() :].strip(" ,")
    else:
        rest = flat

    room_match = re.search(
        r"\b(\d{1,3}[а-яa-z]?(?:/\d{1,3})?(?:\([а-яa-z]\))?)\b",
        rest,
        flags=re.IGNORECASE,
    )
    if room_match:
        room = room_match.group(1)

    teachers = [match.group(0) for match in TEACHER_PATTERN.finditer(flat)]

    return {
        "subject": subject or None,
        "lesson_type": lesson_type,
        "room": room,
        "teachers": teachers,
        "raw_text": raw_text,
    }


def parse_schedule_pdf(path: str) -> ScheduleDocument:
    with pdfplumber.open(path) as pdf:
        page = pdf.pages[0]

        tables = page.find_tables(
            table_settings={
                "vertical_strategy": "lines",
                "horizontal_strategy": "lines",
            }
        )
        if not tables:
            raise ValueError("Не вдалося знайти таблицю з розкладом у PDF.")

        table = tables[0]
        raw_rows = table.extract()

        hlines, vlines = extract_lines(page)
        groups_meta = extract_header_groups(page, table)
        groups = [group["name"] for group in groups_meta]
        semester, academic_year = parse_meta_from_page(page)

        if not groups:
            raise ValueError("Не вдалося зчитати назви груп із заголовка таблиці.")

        slot_starts: list[dict] = []
        current_day: str | None = None

        for row_idx in range(1, len(raw_rows)):
            row = raw_rows[row_idx]

            if row[1]:
                if row[0]:
                    current_day = normalize_day(row[0])

                pair_label, pair_number, time_start, time_end = parse_pair_label(row[1])
                slot_starts.append(
                    {
                        "row_idx": row_idx,
                        "day": current_day or "",
                        "pair_label": pair_label,
                        "pair_number": pair_number,
                        "time_start": time_start,
                        "time_end": time_end,
                    }
                )

        if not slot_starts:
            raise ValueError("Не вдалося знайти часові слоти (пари) у таблиці.")

        entries: list[LessonEntry] = []
        table_bottom = table.bbox[3]

        for slot_index, slot in enumerate(slot_starts):
            row_idx = slot["row_idx"]
            y0 = table.rows[row_idx].cells[1][1]

            if slot_index + 1 < len(slot_starts):
                next_row_idx = slot_starts[slot_index + 1]["row_idx"]
                y1 = table.rows[next_row_idx].cells[1][1]
            else:
                y1 = table_bottom

            col_idx = 0
            while col_idx < len(groups_meta):
                span_start = col_idx
                span_end = col_idx

                while span_end < len(groups_meta) - 1 and not vertical_boundary_exists(
                    vlines=vlines,
                    x=groups_meta[span_end]["x1"],
                    y0=y0 + 0.1,
                    y1=y1 - 0.1,
                ):
                    span_end += 1

                x0 = groups_meta[span_start]["x0"]
                x1 = groups_meta[span_end]["x1"]

                split_ys = find_horizontal_splits(
                    hlines=hlines,
                    x0=x0,
                    x1=x1,
                    y0=y0,
                    y1=y1,
                )

                # protection from noisy inner lines:
                # if many splits are found, keep only the one closest to the middle
                if len(split_ys) > 1:
                    middle = (y0 + y1) / 2
                    split_ys = [min(split_ys, key=lambda y: abs(y - middle))]

                if split_ys:
                    split_y = split_ys[0]
                    regions = [
                        ("NUMERATOR", (x0, y0, x1, split_y)),
                        ("DENOMINATOR", (x0, split_y, x1, y1)),
                    ]
                else:
                    regions = [("ALL", (x0, y0, x1, y1))]

                for week_type, bbox in regions:
                    text = extract_region_text(page, bbox)
                    parsed = parse_lesson_block(text)
                    if not parsed:
                        continue

                    entries.append(
                        LessonEntry(
                            day=slot["day"],
                            pair_number=slot["pair_number"],
                            pair_label=slot["pair_label"],
                            time_start=slot["time_start"],
                            time_end=slot["time_end"],
                            week_type=week_type,
                            groups=[group["name"] for group in groups_meta[span_start : span_end + 1]],
                            subject=parsed["subject"],
                            lesson_type=parsed["lesson_type"],
                            room=parsed["room"],
                            teachers=parsed["teachers"],
                            raw_text=parsed["raw_text"],
                        )
                    )

                col_idx = span_end + 1

        return ScheduleDocument(
            source_file=os.path.basename(path),
            semester=semester,
            academic_year=academic_year,
            groups=groups,
            entries=entries,
        )


@app.post("/parse-schedule", response_model=ScheduleDocument)
async def parse_schedule(file: UploadFile = File(...)) -> ScheduleDocument:
    if not file.filename or not file.filename.lower().endswith(".pdf"):
        raise HTTPException(status_code=400, detail="Потрібно завантажити PDF-файл.")

    tmp_path = None
    try:
        content = await file.read()

        with tempfile.NamedTemporaryFile(delete=False, suffix=".pdf") as tmp:
            tmp.write(content)
            tmp_path = tmp.name

        return parse_schedule_pdf(tmp_path)

    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Помилка парсингу PDF: {exc}") from exc

    finally:
        if tmp_path and os.path.exists(tmp_path):
            os.remove(tmp_path)


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.get("/check-settings")
async def check_settings():
    return {
        "port": settings.PORT,
        "endpoint": settings.STORAGE_ENDPOINT,
        "bucket": settings.STORAGE_BUCKET,
        "key_loaded": bool(settings.STORAGE_SECRET_KEY) # поверне true, якщо ключ зчитано
    }