#!/bin/bash

source .env

if [ -z $user_email ]
then

    echo 'Se debe definir la variable de entorno "user_email" con la casilla de correo del usuario que se conectará por SSH.'
    
else

    # Verifica y eventualmente crea el archivo con las llaves para conectarse por SSH con GCP.
    echo "Verificando llave SSH..."
    sshkey_name=$HOME/.ssh/gcp
    if [ -f "$sshkey_name" ]
    then
        echo "Archivo de clave SSH verificado."
    else
    echo "El archivo de clave SSH no existe."
    echo "Creando archivo de clave..."
    ssh-keygen -f $sshkey_name -t rsa -N '' -C $user_email
    ssh-add $sshkey_name
    fi

    # Terraform init.
    echo "Terraform init..."
    docker run -it --mount type=bind,src=./,dst=/tmp hashicorp/terraform \
        -chdir=/tmp init \
        --reconfigure --var credentials_file_path=/tmp/terraform.json \
        --backend-config bucket="unlu-tf" \
        --backend-config prefix="gke/state" \
        --backend-config credentials=/tmp/terraform.json

    # Terraform destroy.
    echo "Terraform destroy..."
    docker run -it --mount type=bind,src=./,dst=/tmp hashicorp/terraform -chdir=/tmp destroy --auto-approve -lock=false

fi