# AgendaStyle

[![CI](https://github.com/LucassProgramming/agendastyle/actions/workflows/ci.yml/badge.svg)](https://github.com/LucassProgramming/agendastyle/actions/workflows/ci.yml)

AgendaStyle is a full-stack appointment management application for hair salons.

I started this project to improve my knowledge of Spring Boot and, at the same time, learn how a real application can be containerized, deployed to AWS and updated automatically using CI/CD.

## Overview

The application allows a hair salon to manage:

- Services
- Employees
- Clients
- Employee schedules
- Appointments
- Daily agendas

The backend also includes validation rules to avoid invalid or overlapping appointments.

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Bean Validation
- Maven
- PostgreSQL

### Frontend

- React
- TypeScript
- Vite
- Nginx

### DevOps & Cloud

- Docker
- Docker Compose
- GitHub Actions
- Trivy
- Terraform
- AWS EC2
- Amazon ECR
- AWS IAM
- AWS Systems Manager
- GitHub OIDC

## Current Features

- Create and manage salon services
- Manage employees and clients
- Assign working schedules to employees
- Create appointments
- Cancel appointments
- View the daily agenda
- Check employee availability
- Prevent overlapping appointments
- Run the complete application with Docker Compose

## Architecture

The application currently runs on an AWS EC2 instance using Docker Compose.

```text
Internet
   |
   v
Nginx + React
   |
   | /api/*
   v
Spring Boot
   |
   v
PostgreSQL
```

Nginx serves the frontend and also works as a reverse proxy for API requests.

The Spring Boot backend is not exposed directly to the Internet. Requests to `/api` are forwarded from Nginx to the backend through the internal Docker network.

PostgreSQL is also only available inside the Docker environment.

## CI/CD

The project uses GitHub Actions to automatically check and deploy changes pushed to the `main` branch.

The pipeline currently:

1. Runs the backend tests.
2. Runs the frontend lint and build.
3. Builds the Docker images.
4. Scans the images with Trivy for HIGH and CRITICAL vulnerabilities.
5. Connects GitHub Actions to AWS using OIDC.
6. Pushes the Docker images to Amazon ECR.
7. Uses the Git commit SHA as the image version.
8. Deploys the new version to EC2 using AWS Systems Manager.
9. Runs a small smoke test to check that the application responds correctly.

This means I do not need to manually connect to the server every time I want to deploy a new version.

AWS credentials are not stored permanently in GitHub. GitHub Actions gets temporary AWS credentials through OIDC.

## Infrastructure

The AWS infrastructure is managed with Terraform.

The Terraform configuration currently creates and manages resources such as:

- EC2 instance
- Security Group rules
- IAM roles and policies
- EC2 instance profile
- Amazon ECR repositories
- GitHub OIDC provider

The Terraform files are located in:

```text
infrastructure/terraform/
```

Using Terraform allows me to keep the infrastructure configuration in code instead of creating everything manually from the AWS Console.

## Local Development

### Requirements

- Docker
- Docker Compose

Create the local environment file:

```bash
cp .env.example .env
```

Start the application:

```bash
docker compose up --build
```

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8080
```

In the local environment, the backend port is exposed directly to make development and testing easier.

## Production Deployment

Production uses a separate Compose file:

```text
docker-compose.prod.yml
```

The production flow is:

```text
GitHub
   |
   v
GitHub Actions
   |
   v
Amazon ECR
   |
   v
AWS Systems Manager
   |
   v
EC2
   |
   v
Docker Compose
```

Docker images are tagged with the Git commit SHA instead of using only `latest`.

This makes it easier to know exactly which version of the code is running on the server.

## Security

Some security-related improvements already included in the project are:

- GitHub Actions authenticates with AWS through OIDC
- No permanent AWS access keys are stored in GitHub
- Docker images are scanned with Trivy
- The backend port is not publicly exposed in production
- PostgreSQL is not publicly exposed
- Environment files are excluded from Git
- SSH access to EC2 is restricted by IP
- AWS permissions are limited to the resources needed by the project

## Documentation

More detailed notes about the infrastructure and deployment process can be found in:

- [Terraform infrastructure](docs/terraform-infrastructure.md)
- [Manual EC2 deployment](docs/manual-ec2-deployment.md)
- [Technical decisions](docs/technical-decisions.md)

## Future Improvements

There are still several things I want to improve as I continue developing the project:

- Authentication with Spring Security and JWT
- User roles and authorization
- Database migrations with Flyway
- Integration tests with Testcontainers
- Move the production database to Amazon RDS
- HTTPS and a custom domain
- Better monitoring and logs
- Automatic rollback if a deployment fails
- Improve the frontend design and user experience

## Project Status

AgendaStyle is still under development.

At the moment, the main backend functionality, Docker environment, AWS infrastructure and CI/CD pipeline are working.

The next steps will focus mainly on improving the frontend, adding application security and making the database setup more production-ready.