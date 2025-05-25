#!/bin/bash

# Termina la ejecución del script, si algún comando termina con un código de salida diferente de cero.
set -e

# Se asegura de que el CWD sea el del directorio de este script.
cd "$(dirname "$0")"

## Carga el mail con el que se va a acceder a GCP.
#source .env

#if [ -z $user_email ]
#then

#    echo 'Se debe definir la variable de entorno "user_email" con la casilla de correo del usuario que se conectará por SSH.'

#else

#    # Verifica y eventualmente crea el archivo con las llaves para conectarse por SSH con GCP.
#    echo "Verificando clave SSH..."
#    sshkey_name=$HOME/.ssh/gcp
#    if [ -f "$sshkey_name" ]
#    then
#        echo "Archivo de clave SSH verificado."
#    else
#    echo "El archivo de clave SSH no existe."
#    echo "Creando archivo de clave SSH..."
#    ssh-keygen -f $sshkey_name -t rsa -N '' -C $user_email
#    ssh-add $sshkey_name
#    echo "Archivo creado."
#    fi

    # Crea los objetos Kubernetes.
    echo "Se aplicarán los cambios de todos los archivos de configuración Kubernetes..."
    kubectl apply \
        -f 00-back-secrets.yaml \
        -f 00-bd-secrets.yaml \
        -f 00-pvc.yaml \
        -f 01-service-bd.yaml \
        -f 02-service-back.yaml \
        -f 03-deploy-bd.yaml \
        -f 04-deploy-back.yaml
    echo "Cambios aplicados."

#fi