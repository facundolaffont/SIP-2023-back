# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_firewall
resource "google_compute_firewall" "allow-ssh" {
  name    = "allow-ssh"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = ["0.0.0.0/0"]
}

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_firewall
# Permite el tráfico HTTP.
resource "google_compute_firewall" "default" {
  name    = "allow-http-traffic"
  network = "default"

  allow {
    ports    = ["80"]
    protocol = "tcp"
  }

  allow {
    ports    = ["53"]
    protocol = "udp"
  }

  source_ranges = ["0.0.0.0/0"]

}