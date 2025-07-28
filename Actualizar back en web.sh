# Construye la imagen de Docker, la sube al repositorio de Docker Hub,
# y la despliega en la nube.

if [ -z $1 ]; then

    echo "Debe especificar la versión de la imagen."

else

    set -e

    # Construye la imagen de Docker y la sube a DockerHub.
    echo "Construyendo la imagen de Docker..."
    ./gradlew build
    docker build --no-cache -t facundol/sip-backend:$1 .
    docker push facundol/sip-backend:$1
    echo "Imagen de Docker construida."

    # Despliega la imagen en la nube.
    echo "Desplegando la imagen en la nube..."
    kubectl delete -f src/k8s/04-deploy-back.yaml
    kubectl apply -f src/k8s/04-deploy-back.yaml
    echo "Imagen desplegada en la nube."

fi