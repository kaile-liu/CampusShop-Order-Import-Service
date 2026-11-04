# CampusShop Order Management System

A Java Spring Boot application for importing, validating, and managing customer orders from external data sources.

## Overview

The application provides a RESTful API for handling order imports and management, transforming external order data into internal domain models and persisting them for retrieval and modification.

## Technologies

- **Java** – Core application logic and object-oriented design
- **Spring Boot** – Application framework and REST API development
- **JUnit 5 & Mockito** – Unit testing and dependency mocking
- **MapStruct** – DTO-to-domain object mapping
- **Maven** – Dependency management and build automation
- **Docker** – Containerisation and testing

## Features

- Import and validate external order data
- Map partner DTOs to internal domain models
- Generate unique order identifiers and timestamps
- Retrieve and delete existing orders
- Detect and handle duplicate orders
- JSON-based data persistence
- Centralised exception handling and structured logging
- Automated unit tests for service-layer functionality

## Build, run, and test

```bash
./mvnw clean package          # build + run tests
./mvnw spring-boot:run        # run locally on :8080

# Docker
docker build -t ilp:cw1 .
docker run -p 8080:8080 -e ORDER_STORAGE_FILE=/app/data/orders.json ilp:cw1
docker save ilp:cw1 -o ilp_submission_image.tar
```