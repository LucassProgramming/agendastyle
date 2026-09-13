# Technical Decisions

This document summarizes some of the main technical decisions made during the development of AgendaStyle.

## Docker

I use Docker to run the backend, frontend and PostgreSQL in isolated containers.

This makes the application easier to run in different environments because the required dependencies are defined in the Docker configuration.

## Docker Compose

Docker Compose is used to run the complete application as a group of services.

For local development I use:

```text
docker-compose.yml
```

For the EC2 deployment I use:

```text
docker-compose.prod.yml
```

The production configuration is slightly different because the backend is not exposed directly to the Internet.

## Nginx

Nginx serves the React frontend and works as a reverse proxy.

Requests to:

```text
/api/*
```

are forwarded internally to the Spring Boot backend.

This means users only need to access the frontend, while the backend remains inside the Docker network.

## Amazon ECR

Amazon ECR stores the Docker images created by GitHub Actions.

Each image is tagged using the Git commit SHA.

This allows me to identify exactly which application version was deployed.

## Terraform

Terraform is used to define the AWS infrastructure as code.

Instead of manually creating all resources from the AWS Console, the infrastructure configuration is stored together with the project.

## GitHub Actions

GitHub Actions runs the CI/CD pipeline.

Before deploying a change, the pipeline runs tests, frontend checks and container vulnerability scans.

If those steps succeed, the Docker images are published to ECR and deployed to EC2.

## OIDC

GitHub Actions authenticates with AWS through OIDC.

I chose this approach so that I do not need to store permanent AWS access keys inside GitHub.

GitHub receives temporary credentials when the workflow runs.

## AWS Systems Manager

AWS Systems Manager is used to send deployment commands to the EC2 instance.

This means the deployment pipeline does not need to connect to the server through SSH.

## Trivy

Trivy scans the backend and frontend Docker images for HIGH and CRITICAL vulnerabilities before deployment.

If a vulnerability at one of those severity levels is detected, the pipeline fails and the application is not deployed.