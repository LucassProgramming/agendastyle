output "instance_id" {
  description = "AgendaStyle EC2 instance ID"
  value       = aws_instance.agendastyle.id
}

output "public_ip" {
  description = "AgendaStyle EC2 public IP"
  value       = aws_instance.agendastyle.public_ip
}

output "public_dns" {
  description = "AgendaStyle EC2 public DNS"
  value       = aws_instance.agendastyle.public_dns
}
output "backend_ecr_repository_url" {
  description = "ECR repository URL for the backend"
  value       = aws_ecr_repository.backend.repository_url
}

output "frontend_ecr_repository_url" {
  description = "ECR repository URL for the frontend"
  value       = aws_ecr_repository.frontend.repository_url
}
output "github_actions_role_arn" {
  description = "IAM Role ARN used by GitHub Actions through OIDC"
  value       = aws_iam_role.github_actions.arn
}