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