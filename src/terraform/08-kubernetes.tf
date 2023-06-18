# https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_cluster
resource "google_container_cluster" "primary" {
  name                     = "primary"
  location                 = "us-central1-a"
  remove_default_node_pool = true
  initial_node_count       = 1
  network                  = google_compute_network.main.self_link
  subnetwork               = google_compute_subnetwork.private.self_link
  logging_service          = "logging.googleapis.com/kubernetes"
  monitoring_service       = "monitoring.googleapis.com/kubernetes"

  # https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_cluster#networking_mode
  networking_mode          = "VPC_NATIVE"

  addons_config {
    http_load_balancing {
      disabled = false
    }
    horizontal_pod_autoscaling {
      disabled = false
    }
  }

  release_channel {
    # https://cloud.google.com/kubernetes-engine/docs/concepts/release-channels?hl=es-419
    channel = "RAPID"
  }

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  # https://registry.terraform.io/providers/hashicorp/google/latest/docs/resources/container_cluster#nested_ip_allocation_policy
  ip_allocation_policy {
    cluster_secondary_range_name  = "k8s-pod-range"
    services_secondary_range_name = "k8s-service-range"
  }

  private_cluster_config {
    enable_private_nodes    = true
    enable_private_endpoint = false
    master_ipv4_cidr_block  = "172.16.0.0/28"
  }

  maintenance_policy {
    daily_maintenance_window {
      start_time = "03:00"
    }
  }

}