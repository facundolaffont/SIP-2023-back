# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/compute_global_address
resource "google_compute_global_address" "traefik-ip" {
  name = "traefik-ip"
}