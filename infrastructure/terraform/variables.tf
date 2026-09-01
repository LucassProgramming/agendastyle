variable "ssh_cidr" {
  description = "Public IP allowed to connect to the EC2 instance through SSH"
  type        = string
}
variable "instance_type" {
  description = "EC2 instance type for AgendaStyle"
  type        = string
  default     = "t3.micro"
}
variable "aws_region" {
  description = "AWS region where AgendaStyle infrastructure will be deployed"
  type        = string
  default     = "eu-south-2"
}