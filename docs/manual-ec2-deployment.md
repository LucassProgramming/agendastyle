# Manual EC2 Deployment

AgendaStyle was manually deployed to AWS EC2 as a first step before introducing Infrastructure as Code and automated deployments.

## Infrastructure

- AWS EC2
- Amazon Linux 2023
- t3.micro instance
- Docker
- Docker Compose
- Docker Buildx
- PostgreSQL 17
- Spring Boot backend
- React frontend served by Nginx

## Security

- SSH access restricted to the developer IP through the EC2 Security Group.
- Frontend exposed on port 5173.
- Backend API exposed on port 8080.
- PostgreSQL is not exposed through the AWS Security Group.
- GitHub repository access uses a read-only SSH Deploy Key.
- AWS CLI authentication uses temporary credentials through an IAM user with MFA.

## Deployment process

1. Create and configure the EC2 instance.
2. Connect through SSH.
3. Install Git and Docker.
4. Install Docker Compose and Buildx.
5. Configure a read-only GitHub Deploy Key.
6. Clone the private repository.
7. Create the production `.env`.
8. Build the Docker images.
9. Start the stack with Docker Compose.
10. Verify the deployment from the public EC2 address.

## Current limitations

This is intentionally a manual first deployment.

Future improvements include:

- Terraform for Infrastructure as Code.
- Amazon ECR for Docker images.
- Automated CI/CD.
- HTTPS and domain configuration.
- Reduced public exposure of the backend.
- More production-ready database infrastructure.
