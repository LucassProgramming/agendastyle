#!/bin/bash

set -euxo pipefail

dnf update -y
dnf install -y git docker

systemctl enable docker
systemctl start docker

# Allows ec2-user use docker without sudo
usermod -aG docker ec2-user

# Docker CLI plugins
mkdir -p /usr/local/lib/docker/cli-plugins

# Docker Compose
curl -SL \
  https://github.com/docker/compose/releases/download/v5.5.0/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose

chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

mkdir -p /opt/agendastyle
chown ec2-user:ec2-user /opt/agendastyle