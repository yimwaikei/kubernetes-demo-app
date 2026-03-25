# kubernetes-demo-app

A demonstration project showcasing full-stack development and DevOps skills. This repository contains a web application, a backend application, supporting scripts and Kubernetes deployment configurations, all organized within a single monorepo.

## Tech Stack
### Web Application
- React
- TypeScript
- Vite
- Nginx

### Backend
- Kotlin
- Spring Boot
- Maven

### DevOps / Infrastructure
- Docker
- Kubernetes (via Minikube for local development)
- Github Container Registry
- Helm

### Data & Storage
- PostgreSQL
- MinIO

### Scripting & Automation
- Python

## Prerequisites

These tools are required to run the project:

- **Windows Subsystem for Linux 2 (WSL2)** (if on Windows)
- **Java / JDK v25** (for Spring Boot backend; can use IntelliJ IDEA’s bundled JDK)
- **Node.js v24.14.0** (for frontend)
- **pnpm v10.32.1** (package manager)
- **Docker Desktop** (for containerization)
- **Minikube** (local Kubernetes cluster; runs nodes as Docker containers)
- **Python v3.14.3** (for scripting)
- **Helm v4.1.3** (for managing Kubernetes Application)

## Tools (Optional / Helpful)

These tools can make development and testing easier:

- **IntelliJ IDEA** (backend development)
- **Visual Studio Code** (frontend or general development)
- **Git** (version control)
- **Postman** (API testing)
- **DBeaver** (database client for PostgreSQL)
- **Minikube installer** (simplifies Minikube setup)

## Project Structure
```
kubernetes-demo-app/
├── backend/ # Backend API service (Kotlin/Spring Boot/Maven)
├── frontend/ # Web application (React/TypeScript/Vite/Nginx)
├── helm/ # Helm Charts
├── k8s/ # Kubernetes manifests
├── scripts/ # Scripts for jobs
└── README.md
```
