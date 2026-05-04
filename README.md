# 🛒 ESPRIT Market

> **Full-stack marketplace platform** built for the ESPRIT engineering school community — connecting students, sellers, and experts through an intelligent, event-driven e-commerce experience.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Modules](#modules)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Run with Docker Compose](#run-with-docker-compose)
  - [Run with Kubernetes](#run-with-kubernetes)
- [CI/CD Pipeline](#cicd-pipeline)
- [ML Module — Object Detection](#ml-module--object-detection)
- [Environment Variables](#environment-variables)
- [Team](#team)

---

## Overview

**ESPRIT Market** is a multi-module marketplace platform that enables:

- 🛍️ **Sellers** to list products, manage stores, and host events/workshops
- 🧑‍🎓 **Students/Customers** to browse, purchase, and attend live events
- 🤖 **AI-powered features** including dynamic pricing, object detection for product listing, and an AI chatbot
- 📊 **Admins** to manage users, moderate content, and monitor platform activity

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Client Browser                   │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP (port 80)
┌──────────────────────▼──────────────────────────────┐
│             Frontend — Angular 17 (Nginx)           │
│         Proxies /api/* → backend:8081               │
└──────────────────────┬──────────────────────────────┘
                       │ REST / WebSocket
┌──────────────────────▼──────────────────────────────┐
│          Backend — Spring Boot 3 (port 8081)        │
│   JWT Auth · REST API · WebSocket · Scheduler       │
└──────────┬───────────────────────┬──────────────────┘
           │                       │
┌──────────▼──────────┐   ┌────────▼────────────────┐
│   MySQL 8.4         │   │  ML Inference Service   │
│   (port 3306)       │   │  FastAPI + YOLOv8       │
│   esprit_market DB  │   │  (port 8000)            │
└─────────────────────┘   └─────────────────────────┘
```

---

## Modules

| Module | Description |
|--------|-------------|
| `auth` | JWT-based authentication, Google OAuth2, cookie management |
| `user` | User profiles, roles (STUDENT, SELLER, EXPERT, ADMIN) |
| `store` | Store creation, product management, stock movements |
| `event` | Events, tickets, live sessions, dynamic pricing, chat |
| `order` | Orders, delivery tracking, payment integration |
| `marketing` | Promotions, campaigns, discount management |
| `administration` | Platform-wide admin tools and moderation |
| `service` | Expert workshops, certifications, and services |
| `ml` | YOLOv8 object detection for automated product categorization |

---

## Tech Stack

### Backend
- **Java 17** · **Spring Boot 3**
- Spring Security · Spring Data JPA · Hibernate
- WebSocket (STOMP) for live chat
- MySQL 8.4
- Maven

### Frontend
- **Angular 17** (standalone components)
- Angular Material · RxJS
- Chart.js for statistics dashboards
- Nginx for production serving

### DevOps & Infrastructure
- **Docker** & **Docker Compose** (single-VM full stack)
- **Kubernetes** (multi-manifest deployment with Ingress)
- **GitHub Actions** CI/CD → **Azure Web Apps** deployment
- **SonarQube** for code quality analysis

### AI / ML
- **YOLOv8** (Ultralytics) for object detection
- **FastAPI** for the inference REST API
- OpenImages V7 dataset

---

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & Docker Compose v2+
- OR a Kubernetes cluster (e.g. Azure AKS, Minikube)
- Java 17 + Maven (for local backend development)
- Node.js 20+ (for local frontend development)

---

### Run with Docker Compose

This is the **simplest** way to run the full stack locally or on a VM.

```bash
# Clone the repository
git clone https://github.com/ahmed4gaddes/S.A.EspritMarket.git
cd S.A.EspritMarket
git checkout feat/docker-project-sync

# Start all services (MySQL + Backend + Frontend)
docker compose up -d
```

| Service | URL |
|---------|-----|
| Frontend (UI) | http://localhost |
| Backend API | http://localhost:8081 |
| MySQL | localhost:3306 |

> The backend waits for MySQL to pass its health check before starting. No manual DB setup required — the schema is created automatically via Hibernate.

**Stop the stack:**
```bash
docker compose down
```

**Stop and remove volumes (full reset):**
```bash
docker compose down -v
```

---

### Run with Kubernetes

Manifests are in the `k8s/` directory.

```bash
# Apply all manifests in order
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-mysql.yaml
kubectl apply -f k8s/02-backend.yaml
kubectl apply -f k8s/03-frontend.yaml
kubectl apply -f k8s/04-ingress.yaml
```

Or apply them all at once:
```bash
kubectl apply -f k8s/
```

Check pod status:
```bash
kubectl get pods -n esprit-market
```

> The Ingress exposes the frontend on port 80. Make sure an Ingress controller (e.g., `nginx-ingress`) is installed in your cluster.

---

## CI/CD Pipeline

GitHub Actions pipelines are triggered on every push to `feat/docker-project-sync`.

| Pipeline | File | Target |
|----------|------|--------|
| Backend Build & Deploy | `.github/workflows/feat-docker-project-sync_esprit-market-backend.yml` | Azure Web App `esprit-market-backend` |
| Frontend Build & Deploy | `.github/workflows/feat-docker-project-sync_esprit-market-frontend.yml` | Azure Web App `esprit-market-frontend` |

**Backend pipeline steps:**
1. Checkout code
2. Set up Java 17 (Microsoft distribution)
3. `mvn clean install -DskipTests`
4. Upload JAR artifact
5. Deploy to Azure App Service (Production slot)

**Required GitHub Secrets:**

| Secret | Description |
|--------|-------------|
| `AZUREAPPSERVICE_CLIENTID_*` | Azure AD application client ID |
| `AZUREAPPSERVICE_TENANTID_*` | Azure AD tenant ID |
| `AZUREAPPSERVICE_SUBSCRIPTIONID_*` | Azure subscription ID |

---

## ML Module — Object Detection

The `ml/` directory contains a complete YOLOv8 pipeline for detecting product categories from images.

### Quick Start (Inference only)

```powershell
# From repo root
python -m venv .venv-py312
.\.venv-py312\Scripts\Activate.ps1
pip install -r ml/requirements-inference.txt

# Run the FastAPI inference server
$env:ESPRIT_OD_MODEL = "runs\detect\train\weights\best.pt"
.\.venv-py312\Scripts\uvicorn.exe ml.inference.app:app --host 127.0.0.1 --port 8000
```

### Training Pipeline

```powershell
pip install -r ml/requirements-train.txt

# 1. Download Open Images V7 dataset
python ml/openimages/download_openimages_v7.py

# 2. Clean the YOLO export
python ml/openimages/clean_yolo_dataset.py --dataset_dir ml/openimages/out/YOUR_DATASET --split val --write_cleaned

# 3. Train YOLOv8
yolo detect train data=ml/openimages/out/YOUR_DATASET/dataset.yaml model=yolov8n.pt epochs=60 imgsz=640 device=0
```

> See [`ml/README.md`](ml/README.md) for the full detailed guide.

---

## Environment Variables

### Backend (`docker-compose.yml` / K8s secrets)

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:mysql://mysql:3306/esprit_market` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `root` |
| `APP_PUBLIC_BASE_URL` | Public base URL for file serving | `""` |

### ML Inference

| Variable | Description |
|----------|-------------|
| `ESPRIT_OD_MODEL` | Path to the `best.pt` weights file |

---

## Team

Developed by the **ESPRIT 4th-year Software Engineering** team as part of the **Advanced Software Engineering** project.

| Module | Developer |
|--------|-----------|
| Auth & User | Team Member |
| Store & Products | Team Member |
| Events & Live | Team Member |
| Orders & Delivery | Team Member |
| Marketing | Team Member |
| Administration | Team Member |
| ML / AI | Team Member |

---

<p align="center">
  Built with ❤️ at <strong>ESPRIT School of Engineering</strong>
</p>
