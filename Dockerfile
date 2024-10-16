FROM python:3.8-slim-buster
WORKDIR /app
ADD app.py /app
RUN pip install --no-cache-dir flask requests gunicorn
EXPOSE 5000
CMD ["gunicorn", "-w", "2", "-b", "0.0.0.0:5000", "app:app"]
