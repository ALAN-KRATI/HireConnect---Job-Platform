# HireConnect---Job-Platform

# Backend

HireConnect Backend is a microservices-based recruitment platform built using Spring Boot. It provides separate services for authentication, profile management, job posting, applications, interviews, notifications, subscriptions, and analytics.

The backend is designed so that candidates can search and apply for jobs, while recruiters can post jobs, manage applications, schedule interviews, and monitor hiring performance.

---

# Architecture

                    +-------------------+
                    |   React Frontend  |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    |    API Gateway    |
                    |      Port 8080    |
                    +---------+---------+
                              |
        -----------------------------------------------------
        |        |         |         |         |            |
        v        v         v         v         v            v
   Auth     Profile      Job    Application Interview Notification
  8081        8082      8083       8084         8085        8086

                              |
                              v
                  Subscription Service (8087)
                              |
                              v
                    Analytics Service (8088)

---

# Tech Stack

* Java 21
* Spring Boot 3
* Spring Security
* JWT Authentication
* OAuth2 Login with GitHub
* SQL
* RabbitMQ
* Maven
* Docker and Docker Compose
* Lombok
* RestTemplate
* OpenFeign
* Spring Cloud Gateway

---

# Microservices

| Service              | Port | Base Package                   | Responsibility                   |
| -------------------- | ---- | ------------------------------ | -------------------------------- |
| auth-service         | 8081 | `com.hireconnect.auth`         | Registration, login, JWT, OAuth2 |
| profile-service      | 8082 | `com.hireconnect.profile`      | Candidate and recruiter profiles |
| job-service          | 8083 | `com.hireconnect.job`          | Job posting, search, filters     |
| application-service  | 8084 | `com.hireconnect.application`  | Job applications and status      |
| interview-service    | 8085 | `com.hireconnect.interview`    | Interview scheduling             |
| notification-service | 8086 | `com.hireconnect.notification` | Email and in-app notifications   |
| subscription-service | 8087 | `com.hireconnect.subscription` | Recruiter plans and payments     |
| analytics-service    | 8088 | `com.hireconnect.analytics`    | Hiring metrics and reports       |
| api-gateway          | 8080 | `com.hireconnect.gateway`      | Single entry point for frontend  |

---

# Main Features

## Authentication

* User registration and login
* JWT token generation and validation
* GitHub OAuth2 login
* Logout and refresh token support
* Role-based access:

  * `CANDIDATE`
  * `RECRUITER`

Example endpoints:

```text
POST /auth/register
POST /auth/login
POST /auth/logout
POST /auth/refresh
```

---

## Candidate Features

A candidate can:

* Create and update profile
* Upload resume
* Search jobs
* Filter jobs by:

  * Title
  * Location
  * Salary
  * Experience
  * Category
* Apply for jobs
* Add cover letter
* Save or bookmark jobs
* Track application status

Possible statuses:

APPLIED
SHORTLISTED
INTERVIEW_SCHEDULED
OFFERED
REJECTED

---

## Recruiter Features

A recruiter can:

* Create company profile
* Post jobs
* Edit, pause, or delete jobs
* View applications
* Shortlist or reject candidates
* Schedule interviews
* Send interview invitations
* View resumes
* View hiring analytics

---

# Service Details

## Auth Service

Responsible for:

* Registration
* Login
* Password hashing
* JWT generation
* Token validation
* GitHub OAuth2

---

## Profile Service

Stores:

* Candidate profile
* Recruiter profile
* Company details
* Skills
* Experience
* Education
* Resume URL

---

## Job Service

Handles:

* Job creation
* Job update
* Job deletion
* Job search
* Filtering jobs

---

## Application Service

Responsible for:

* Applying for jobs
* Tracking status
* Recruiter reviewing applications

---

## Interview Service

Responsible for:

* Scheduling interviews
* Sending meeting details
* Updating interview status

Interview details include:

* Date
* Time
* Mode
* Meeting link
* Recruiter notes

---

## Notification Service

Handles:

* Email notifications
* In-app notifications
* Job alerts
* Interview alerts
* Status update alerts

RabbitMQ is used for async notifications.

Example flow:


Application Status Changed
        |
        v
RabbitMQ Queue
        |
        v
Notification Service
        |
        v
Email + In-App Notification

---

## Subscription Service

Recruiters have subscription plans.

### Available Plans

| Plan         | Features                             |
| ------------ | ------------------------------------ |
| Free         | Limited job postings                 |
| Professional | More job postings and analytics      |
| Enterprise   | Unlimited access and team management |

Supported payment methods:

* Card

---

## Analytics Service

Provides:

* Number of applications
* Number of interviews
* Recruiter dashboard metrics

---

# API Gateway Routes

/auth/**          -> auth-service
/profile/**       -> profile-service
/jobs/**          -> job-service
/applications/**  -> application-service
/interviews/**    -> interview-service
/notifications/** -> notification-service
/subscriptions/** -> subscription-service
/analytics/**     -> analytics-service

---

# Inter-Service Communication

HireConnect uses two types of communication:

## Synchronous Communication

Used when one service immediately needs data from another service.

Tools used:

* RestTemplate
* OpenFeign

---

## Asynchronous Communication

Used for notifications and background events.

Tool used:

* RabbitMQ

Example:

application-service
        |
        v
Publishes Event
        |
        v
notification-service receives event

---

# Security

The backend uses JWT-based authentication.

Flow:

1. User logs in
2. Backend returns JWT token
3. Frontend sends token in header
4. JWT filter validates the token
5. User gets access based on role

Protected routes require authentication.

---

# Running the Project

## Clone Repository

```bash
git clone <repo-url>
cd hireconnect-backend
```

## Build Project

```bash
mvn clean install
```

## Start Docker Services

```bash
docker-compose up
```

---

# Future Improvements

* AI-based job recommendation
* Real-time chat between recruiter and candidate
* Video interview support

---

# Folder Structure

hireconnect-backend/
│
├── auth-service/
├── profile-service/
├── job-service/
├── application-service/
├── interview-service/
├── notification-service/
├── subscription-service/
├── analytics-service/
├── api-gateway/
├── docker-compose.yml
└── README.md

