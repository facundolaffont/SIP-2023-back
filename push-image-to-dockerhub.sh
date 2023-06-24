# Construye la imagen de Docker y la sube al repositorio de Docker Hub.

set -e

./gradlew build
docker build -t facundol/sip-backend:latest .
docker push facundol/sip-backend:latest