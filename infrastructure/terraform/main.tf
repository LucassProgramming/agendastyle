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
  iam_instance_profile        = aws_iam_instance_profile.agendastyle.name
  vpc_security_group_ids      = [aws_security_group.agendastyle.id]
  associate_public_ip_address = true
  user_data                   = file("${path.module}/user_data.sh")
  user_data_replace_on_change = true

  root_block_device {
    volume_size = 16
    volume_type = "gp3"
  }

  tags = {
    Name    = "agendastyle-server-tf"
    Project = "AgendaStyle"
  }

}
resource "aws_ecr_repository" "backend" {
  name                 = "agendastyle-backend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Project = "AgendaStyle"
  }
}

resource "aws_ecr_repository" "frontend" {
  name                 = "agendastyle-frontend"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Project = "AgendaStyle"
  }
}
resource "aws_iam_role" "ec2_ecr" {
  name = "agendastyle-ec2-ecr-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "ec2.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Project = "AgendaStyle"
  }
}

resource "aws_iam_role_policy_attachment" "ec2_ecr_read_only" {
  role       = aws_iam_role.ec2_ecr.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

resource "aws_iam_instance_profile" "agendastyle" {
  name = "agendastyle-ec2-profile"
  role = aws_iam_role.ec2_ecr.name
}
resource "aws_iam_openid_connect_provider" "github" {
  url = "https://token.actions.githubusercontent.com"

  client_id_list = [
    "sts.amazonaws.com"
  ]

  tags = {
    Project = "AgendaStyle"
  }
}
resource "aws_iam_role" "github_actions" {
  name = "agendastyle-github-actions-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Federated = aws_iam_openid_connect_provider.github.arn
        }

        Action = "sts:AssumeRoleWithWebIdentity"

        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
            "token.actions.githubusercontent.com:sub" = "repo:LucassProgramming@201976559/agendastyle@1344726885:ref:refs/heads/main"
          }
        }
      }
    ]
  })

  tags = {
    Project = "AgendaStyle"
  }
}

resource "aws_iam_role_policy" "github_actions_ecr" {
  name = "agendastyle-github-actions-ecr"
  role = aws_iam_role.github_actions.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Sid    = "GetECRAuthorizationToken"
        Effect = "Allow"

        Action = [
          "ecr:GetAuthorizationToken"
        ]

        Resource = "*"
      },
      {
        Sid    = "PushImagesToAgendaStyleRepositories"
        Effect = "Allow"

        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:BatchGetImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage"
        ]

        Resource = [
          aws_ecr_repository.backend.arn,
          aws_ecr_repository.frontend.arn
        ]
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "ec2_ssm" {
  role       = aws_iam_role.ec2_ecr.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}
