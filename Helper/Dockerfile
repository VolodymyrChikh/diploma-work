FROM python:3.11-slim

WORKDIR /app

# Заборонoutput буферизації логів (щоб відразу бачити принти в Docker Desktop)
ENV PYTHONUNBUFFERED=1

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000
# Використовуємо змінну $PORT, яку виставлятиме Docker Compose або Google Cloud Run
CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --port ${PORT:-8000}"]