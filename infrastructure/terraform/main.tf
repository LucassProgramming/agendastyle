data "aws_vpc" "default" {
  default = true
}
data "aws_ami" "amazon_linux_2023" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-x86_64"]
  }

  filter {
    name   = "architecture"
    values = ["x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

data "aws_key_pair" "agendastyle" {
  key_name = "agendastyle-key"
}

resource "aws_security_group" "agendastyle" {
  name        = "agendastyle-sg-tf"
  description = "Security group for AgendaStyle"
  vpc_id      = data.aws_vpc.default.id

  tags = {
    Name    = "agendastyle-sg-tf"
    Project = "AgendaStyle"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ssh" {
  security_group_id = aws_security_group.agendastyle.id
  cidr_ipv4         = var.ssh_cidr
  from_port         = 22
  ip_protocol       = "tcp"
  to_port           = 22
}

resource "aws_vpc_security_group_ingress_rule" "frontend" {
  security_group_id = aws_security_group.agendastyle.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 5173
  ip_protocol       = "tcp"
  to_port           = 5173
}

resource "aws_vpc_security_group_ingress_rule" "backend" {
  security_group_id = aws_security_group.agendastyle.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 8080
  ip_protocol       = "tcp"
  to_port           = 8080
}

resource "aws_vpc_security_group_egress_rule" "all" {
  security_group_id = aws_security_group.agendastyle.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1"
}
resource "aws_instance" "agendastyle" {
  ami                         = data.aws_ami.amazon_linux_2023.id
  instance_type               = var.instance_type
  key_name                    = data.aws_key_pair.agendastyle.key_name
  vpc_security_group_ids      = [aws_security_group.agendastyle.id]
  associate_public_ip_address = true

  root_block_device {
    volume_size = 16
    volume_type = "gp3"
  }

  tags = {
    Name    = "agendastyle-server-tf"
    Project = "AgendaStyle"
  }
}