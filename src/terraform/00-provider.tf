# https://developer.hashicorp.com/terraform/language/settings
terraform {
  
  # https://developer.hashicorp.com/terraform/language/settings#specifying-provider-requirements
  required_providers {
    google = {
      source  = "hashicorp/google"
      version = ">=4.60.0"
    }
  }

  # https://developer.hashicorp.com/terraform/language/settings/backends/configuration
  # Crea la carpeta .terraform y sus archivos.
  backend "gcs" {}

  # https://developer.hashicorp.com/terraform/language/settings#specifying-a-required-terraform-version
  required_version = ">= 1.4.5"
}

# https://developer.hashicorp.com/terraform/language/providers/configuration
provider "google" {
  project     = var.project_id
  region      = var.region
  zone        = var.zone
}