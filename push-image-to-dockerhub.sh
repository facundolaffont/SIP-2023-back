# Construye la imagen de Docker y la sube al repositorio de Docker Hub.

if [ -z $1 ]; then

    echo "Debe especificar la versión de la imagen"

else

    set -e

    ./gradlew build

    docker build --no-cache -t facundol/sip-backend:$1 .
    docker push facundol/sip-backend:$1

fi