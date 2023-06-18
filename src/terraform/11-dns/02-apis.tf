# Crea el registro DNS que resuelve el nombre app.fl.com.ar
# a la IP del balanceador de cargas.
resource "cloudflare_record" "app_loadbalancer" {
  zone_id = data.cloudflare_zone.app.zone_id
  name    = "app"
  type    = "A"
  value   = var.LOADBALANCER_IP
  ttl     = 300
  proxied = false
}