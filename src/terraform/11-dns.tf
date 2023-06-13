# https://cloud.google.com/dns/docs/tutorials/create-domain-tutorial?hl=es-419#terraform

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_instance
resource "google_compute_instance" "default" {
  name         = "dns-compute-instance"
  machine_type = "g1-small"
  zone         = var.zone

  boot_disk {
    initialize_params {
      image = "debian-cloud/debian-11"
    }
  }

  network_interface {
    network = "default"
    access_config {
      # Configuraciones por defecto.
    }
  }

  # Permite verificar que el servidor DNS està activo.
  # Se puede comentar para producción.
  metadata_startup_script = <<-EOF
  sudo apt-get update && \
  sudo apt-get install apache2 -y && \
  echo "<!doctype html><html><body><h1>Server DNS activo!</h1></body></html>" > /var/www/html/index.html
  EOF
}

# Crea una zona DNS.
# resource "random_id" "rnd" {
#   byte_length = 4
# }
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/dns_managed_zone
resource "google_dns_managed_zone" "default" {
#   name          = "example-zone-googlecloudexample"
  name          = "zona-spgda"
#   dns_name      = "example-${random_id.rnd.hex}.com."
  dns_name      = "fl.com.ar."
  description   = "Zona DNS para SPGDA"
  force_destroy = "true"
}

# Registra la IP del server web en el servidor DNS.
# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/dns_record_set
resource "google_dns_record_set" "default" {
  name         = "www.${google_dns_managed_zone.default.dns_name}"
  managed_zone = google_dns_managed_zone.default.name
  type         = "A"
  ttl          = 300
  rrdatas = [
    google_compute_instance.default.network_interface[0].access_config[0].nat_ip
  ]
}