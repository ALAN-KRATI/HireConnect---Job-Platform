# HireConnect Docker Deployment Guide

## Overview

This guide explains how to deploy the entire HireConnect platform using Docker Compose.

## Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+
- At least 4GB RAM allocated to Docker
- Ports 80, 3307, 5672, 6379, 8080-8089, 8761, 9200, 15672 available

## Quick Start

### 1. Environment Setup

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env with your values
nano .env
```

### 2. Build and Start

```bash
# Build all services (first time only)
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 3. Access the Application

- **Frontend**: http://localhost
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## What's Different From Local Development?

### Networking
- **Local**: Services use `localhost` with different ports
- **Docker**: Services use container names as hostnames (e.g., `mysql`, `redis`, `discovery-server`)

### Ports
- **Local**: MySQL on 3306
- **Docker**: MySQL on 3307 (mapped to container's 3306)

### Frontend Connectivity
- **Local**: Frontend at `localhost:5173` → API at `localhost:8080`
- **Docker**: Frontend at `localhost:80` → Nginx proxies `/api/` to `api-gateway:8080`

## Architecture

```
┌─────────────────┐
│   Frontend      │  ← http://localhost (Nginx)
│   (Port 80)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  API Gateway    │  ← http://api-gateway:8080
│   (Port 8080)   │
└────────┬────────┘
         │
    ┌────┴────┬────────┬────────┬────────┬────────┬────────┬────────┬────────┐
    │         │        │        │        │        │        │        │        │
    ▼         ▼        ▼        ▼        ▼        ▼        ▼        ▼        ▼
┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐
│ Auth │ │Profil│ │ Job  │ │ Appl │ │ Intrv│ │ Notif│ │ Subsc│ │ Analy│ │ Web  │
│ 8081 │ │ 8082 │ │ 8083 │ │ 8084 │ │ 8085 │ │ 8086 │ │ 8087 │ │ 8088 │ │ 8089 │
└──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘ └──┬───┘
   │        │        │        │        │        │        │        │        │
   └────────┴────────┴────────┴────────┴────────┴────────┴────────┴────────┘
                                      │
                           ┌──────────┴──────────┐
                           │                     │
                    ┌──────▼──────┐     ┌───────▼───────┐
                    │    MySQL    │     │     Redis     │
                    │   (3306)    │     │    (6379)     │
                    └─────────────┘     └───────────────┘
                           │
                    ┌──────▼──────┐
                    │  RabbitMQ   │
                    │   (5672)    │
                    └─────────────┘
```

## Service Details

| Service | Container Name | Port | Description |
|---------|---------------|------|-------------|
| Frontend | hireconnect-frontend | 80 | React + Vite via Nginx |
| API Gateway | hireconnect-api-gateway | 8080 | Routes all API requests |
| Discovery | hireconnect-discovery-server | 8761 | Eureka service registry |
| Auth | hireconnect-auth-service | 8081 | Authentication & OAuth |
| Profile | hireconnect-profile-service | 8082 | User profiles |
| Job | hireconnect-job-service | 8083 | Job listings |
| Application | hireconnect-application-service | 8084 | Job applications |
| Interview | hireconnect-interview-service | 8085 | Interview scheduling |
| Notification | hireconnect-notification-service | 8086 | Email notifications |
| Subscription | hireconnect-subscription-service | 8087 | Payment plans |
| Analytics | hireconnect-analytics-service | 8088 | Reports & stats |
| Web (Thymeleaf) | hireconnect-web | 8089 | Legacy web interface |
| MySQL | hireconnect-mysql | 3307:3306 | Database |
| Redis | hireconnect-redis | 6379 | Cache & sessions |
| Elasticsearch | hireconnect-elasticsearch | 9200 | Job search |
| RabbitMQ | hireconnect-rabbitmq | 5672, 15672 | Message queue |

## Configuration Changes Made

### 1. Fixed Port Mismatches
- **notification-service**: Changed Dockerfile from `8085` → `8086`
- **subscription-service**: Changed Dockerfile from `8086` → `8087`

### 2. Added Frontend Container
- Created `HireConnect-Frontend/Dockerfile`
- Created `HireConnect-Frontend/nginx.conf` with API proxying
- Added frontend service to docker-compose.yml

### 3. Database Auto-Initialization
- Created `docker/init-databases.sql` to create all databases on startup
- Mounted SQL file to MySQL container's init directory

### 4. Environment Variables
- Created `.env.example` template
- All sensitive configs moved to environment variables

### 5. Docker Optimizations
- Added `.dockerignore` files to all services
- Prevents copying unnecessary files into images

## Troubleshooting

### Services Not Starting
```bash
# Check service logs
docker-compose logs <service-name>

# Example
docker-compose logs auth-service
```

### Database Connection Issues
```bash
# MySQL container must be healthy before other services start
# Check MySQL health
docker-compose ps

# If unhealthy, check logs
docker-compose logs mysql
```

### Eureka Registration Problems
```bash
# Check Eureka dashboard
curl http://localhost:8761/eureka/apps

# Should show all 11 services registered
```

### Frontend Can't Connect to API
```bash
# Verify Nginx is routing correctly
docker exec -it hireconnect-frontend nginx -t

# Check API is reachable from frontend container
docker exec -it hireconnect-frontend wget -qO- http://api-gateway:8080/actuator/health
```

### Clean Slate
```bash
# Remove everything and start fresh
docker-compose down -v  # Removes volumes too
docker system prune -f   # Removes unused images

# Rebuild everything
docker-compose up -d --build
```

## Data Persistence

- **MySQL data**: Stored in `mysql-data` volume
- **Elasticsearch data**: Stored in `elasticsearch-data` volume
- **Logs**: Written to stdout (view with `docker-compose logs`)

## Scaling (Advanced)

```bash
# Scale specific service
# Note: Requires stateless configuration
docker-compose up -d --scale job-service=3
```

## Security Considerations

1. **Never commit `.env` file** with real credentials
2. **Change default passwords** in production
3. **Enable SSL/TLS** for production deployments
4. **Restrict port exposure** to necessary ports only
5. **Regular security updates** for base images

## Production Deployment

For production, consider:

1. **Reverse proxy** (Traefik/Nginx) with SSL termination
2. **Container orchestration** (Kubernetes/Swarm)
3. **Centralized logging** (ELK stack or similar)
4. **Monitoring** (Prometheus/Grafana)
5. **Secrets management** (Vault/AWS Secrets Manager)
6. **CI/CD pipeline** for automated builds

## Maintenance Commands

```bash
# Update images
docker-compose pull
docker-compose up -d

# View resource usage
docker stats

# Backup MySQL
docker exec hireconnect-mysql mysqldump -u root -p hireconnectdb > backup.sql

# Restore MySQL
docker exec -i hireconnect-mysql mysql -u root -p hireconnectdb < backup.sql
```

## Support

For issues specific to Docker deployment, check:
1. Service logs: `docker-compose logs <service>`
2. Container health: `docker-compose ps`
3. Network connectivity: `docker network inspect hireconnect-network`
4. Resource limits: `docker system df`