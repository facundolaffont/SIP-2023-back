# https://developer.hashicorp.com/terraform/language/resources

# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/google_project_service
resource "google_project_service" "compute" {
  service = "compute.googleapis.com"

  disable_dependent_services = true
}

resource "google_project_service" "container" {

  # https://cloud.google.com/kubernetes-engine/docs/reference/rest#service:-container.googleapis.com
  service = "container.googleapis.com"

}

resource "google_project_service" "cloud_resource_manager" {

  # https://cloud.google.com/resource-manager/reference/rest#service:-cloudresourcemanager.googleapis.com
  service            = "cloudresourcemanager.googleapis.com"
  
  disable_on_destroy = false
}