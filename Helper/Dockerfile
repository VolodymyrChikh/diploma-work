FROM python:3.11-slim

WORKDIR /app

# Системні залежності (якщо твій парсер розкладу використовує якісь специфічні ліби)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Копіюємо та встановлюємо залежності Python
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Копіюємо весь код додатка
COPY . .

# Render сам передасть порт у змінну PORT, тому біндимо Uvicorn на неї
EXPOSE 9001

ENTRYPOINT ["sh", "-c", "uvicorn schedule_parser:app --host 0.0.0.0 --port ${PORT:-9001}"]