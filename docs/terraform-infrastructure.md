# Terraform Infrastructure

This folder contains the Terraform configuration used to create the AWS infrastructure for AgendaStyle.

The first deployment of the project was done manually on AWS EC2. After that, I started moving the infrastructure to Terraform so it could be reproduced from code instead of depending on manual configuration in the AWS Console.

## Current setup

Terraform currently creates:

- EC2 instance using Amazon Linux 2023
- `t3.micro` instance
- 16 GB gp3 root volume
- Security Group
- SSH access only from my public IP
- Port 5173 for the frontend
- Port 8080 for the backend

PostgreSQL is not exposed through the AWS Security Group.

Terraform also uses existing AWS resources through data sources:

- Default VPC
- Amazon Linux 2023 AMI
- Existing SSH Key Pair

## Files

```text
infrastructure/terraform/
├── main.tf
├── outputs.tf
├── provider.tf
├── user_data.sh
├── variables.tf
├── versions.tf
└── .terraform.lock.hcl
```

`main.tf` contains the AWS resources.

`variables.tf` contains values that can change between environments, such as the AWS region, instance type and allowed SSH IP.

`outputs.tf` returns useful information after the deployment, such as the EC2 public IP, DNS and instance ID.

`user_data.sh` prepares the EC2 instance automatically when it starts.

## EC2 bootstrap

The EC2 instance uses User Data with `cloud-init`.

At first I was installing everything manually after connecting through SSH. The bootstrap script now installs and configures:

- Git
- Docker
- Docker Compose
- Docker service
- Docker permissions for `ec2-user`

It also creates:

```text
/opt/agendastyle
```

which will be used as the deployment directory.

This means that if Terraform creates a new EC2 instance, I do not have to repeat the basic server setup manually.

## Terraform workflow

The commands I normally use are:

```bash
terraform init
terraform fmt
terraform validate
terraform plan
terraform apply
```

Before applying changes I check the plan first.

For example:

```bash
terraform plan \
  -var="ssh_cidr=$(curl -s https://checkip.amazonaws.com)/32"
```

When I want to apply exactly the reviewed plan:

```bash
terraform plan \
  -var="ssh_cidr=$(curl -s https://checkip.amazonaws.com)/32" \
  -out=tfplan

terraform apply tfplan
```

## Terraform State

Terraform uses the state file to keep track of which AWS resources belong to the Terraform configuration.

For example:

```text
aws_instance.agendastyle
```

is linked through the state to the real EC2 instance created in AWS.

The current state can be checked with:

```bash
terraform state list
```

State files are ignored by Git because they can contain infrastructure details and sensitive information.

The `.terraform.lock.hcl` file is committed because it keeps provider versions consistent.

## User Data changes

The EC2 resource currently uses:

```hcl
user_data_replace_on_change = true
```

If the bootstrap script changes, Terraform replaces the EC2 instance so the new machine starts with the updated configuration.

This was useful while setting up the infrastructure because the server did not contain important application data yet.

## AWS authentication

Terraform does not contain AWS access keys.

For local development I use temporary AWS credentials and an AWS profile.

The goal is to avoid storing permanent AWS credentials in the repository.

## Current limitations

The infrastructure is still a development setup.

Some things that still need to be improved:

- Container images are not stored in ECR yet
- Deployment is still not automatic
- EC2 public IP changes after stop/start
- Backend and frontend ports are directly exposed
- HTTPS is not configured
- PostgreSQL still runs on the same EC2 server
- Terraform State is currently local

## Next steps

The next changes will focus on:

- Amazon ECR
- IAM Role for EC2
- GitHub Actions authentication with AWS
- Building and pushing Docker images from CI
- Automatic deployment to EC2

The idea is to move from:

```text
GitHub → manual EC2 deployment
```

to:

```text
GitHub
  ↓
GitHub Actions
  ↓
Docker images
  ↓
Amazon ECR
  ↓
EC2
```