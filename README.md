
# Klastr Backend

Klastr is a multi-tenant SaaS platform designed to help educational institutions manage training placements, students, and partner companies efficiently.

This repository contains the backend service built with a scalable architecture, prepared for future growth and production-ready patterns.

---

## 🚀 Tech Stack

- Java 21  
- Spring Boot  
- Spring Security  
- Spring Data JPA (Hibernate)  
- PostgreSQL  
- Maven  

---

## 🧠 Architecture

The project follows a layered architecture:

```
controller → service → repository → domain
```

### Core Principles

- Separation of concerns  
- Scalability-first design  
- Multi-tenant ready  
- Clean code practices  
- RESTful API structure  

Klastr is being developed with a production mindset rather than as a simple academic project.

---

## 🏗️ Current Features

✅ Tenant module (foundation of the multi-tenant architecture)  
✅ REST API  
✅ Database persistence with JPA  
✅ Secure endpoints with Spring Security  
✅ Environment-based configuration  

---

## 🔐 Security

Spring Security is configured by default.

For development, a generated password will appear in the console when the application starts:

```
Using generated security password: xxxxxxxx
```

**Username**

```
user
```

---

## ⚙️ Running the Project Locally

### 1️⃣ Clone the repository

```bash
git clone https://github.com/laclipi/klastr-backend.git
```

---

### 2️⃣ Configure environment variables

Create the file:

```
src/main/resources/application-local.properties
```

Example configuration:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/klastrdb
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

### 3️⃣ Run the application

```bash
mvn spring-boot:run
```

Server starts at:

```
http://localhost:8080
```

---

## 🌍 Vision

Klastr aims to become a scalable SaaS solution for educational centers, enabling them to manage the entire training lifecycle from a single platform.

The system is being designed with long-term evolution in mind, including:

- Role-based access  
- Company management  
- Student tracking  
- Agreement workflows  
- Analytics  

---

## 🚧 Project Status

Active development.

Currently building the core domain that will support the rest of the platform.

---

## 👩‍💻 Author

**Laura Climent**  
Backend Developer focused on Java and scalable architectures.
