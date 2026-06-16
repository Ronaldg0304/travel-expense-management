# PROJECT_CONTEXT.md

## 1. Project Overview

### Project Name

Travel Expense Management

### Purpose

Corporate web application for managing travel expense advances, approvals, disbursements, expense reports (legalizations), refunds, reimbursements, and audit tracking.

The system centralizes the complete lifecycle of travel expenses within the organization, ensuring traceability, approval control, accounting allocation, and financial validation.

---

## 2. Current Project Status

### Phase

Analysis and Architecture Completed

### Completed Artifacts

- Business Rules
- Domain Model
- Use Cases
- Permission Matrix
- Enumerations
- Entity Relationship Diagram (ERD)
- Backend Architecture Definition
- Technology Stack Definition

### Current Development Phase

Backend Foundation Setup

---

## 3. Technology Stack

### Backend

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL 8
- Jakarta Validation
- Lombok
- MapStruct
- OpenAPI / Swagger
- JUnit 5
- Mockito

### Frontend

- SvelteKit
- TypeScript
- Tailwind CSS
- Fetch API

### Development Tools

- IntelliJ IDEA
- Visual Studio Code
- Git
- GitHub
- Postman
- OpenCode
- GitHub Copilot

---

## 4. Architectural Principles

### Architecture Style

Modular Monolith

### Package Strategy

Package By Feature

### Design Principles

- SOLID
- Clean Code
- Separation of Concerns
- Single Responsibility Principle
- Explicit Domain Modeling

### Forbidden Practices

- God Classes
- Business Logic Inside Controllers
- Business Logic Inside Mappers
- Entity Exposure Through API
- EnumType.ORDINAL

---

## 5. Authentication Strategy

### Login

Email + Password

### Authorization

JWT

### Roles

- ADMINISTRADOR
- EMPLEADO
- AVALADOR
- FINANCIERA

### Token Strategy

Access Token: 15 minutes

Refresh Token: 7 days

Refresh Tokens stored in database.

---

## 6. Core Business Flow

Solicitud de Viático

↓

Aprobación

↓

Desembolso

↓

Legalización

↓

Validación Financiera

↓

- Cerrada
- Pendiente Devolución
- Pendiente Reembolso

↓

Cierre

---

## 7. Enumerations

### EstadoSolicitud

- BORRADOR
- ENVIADA
- DEVUELTA
- APROBADA
- RECHAZADA
- DESEMBOLSADA
- LEGALIZADA
- LEGALIZACION_DEVUELTA
- VALIDADA
- PENDIENTE_DEVOLUCION
- PENDIENTE_REEMBOLSO
- CERRADA

### Rol

- ADMINISTRADOR
- EMPLEADO
- AVALADOR
- FINANCIERA

### TipoDocumento

- CC
- CE
- PASAPORTE

### TipoDecision

- APROBADA
- DEVUELTA
- RECHAZADA

### TipoCuenta

- AHORROS
- CORRIENTE

### TipoCentroImputacion

- DEPARTAMENTO
- CENTRO_OPERACION

---

## 8. Domain Modules

### Security

Authentication and Authorization

### User

User management

### Department

Department management

### Account

User bank accounts

### TravelRequest

Travel advance requests

### Approval

Approval process

### Disbursement

Travel advance payments

### Legalization

Expense reporting

### Expense

Expense records

### CostCenter

Accounting allocation

### Refund

Employee refund to company

### Reimbursement

Company reimbursement to employee

### Audit

System traceability

---

## 9. Permission Matrix

### EMPLEADO

Can:

- Create requests
- Edit draft requests
- Submit requests
- Correct returned requests
- Create legalizations
- Correct returned legalizations
- Register expenses
- Upload support files
- Register refunds

### AVALADOR

Can:

- Approve requests
- Return requests
- Reject requests

### FINANCIERA

Can:

- Register disbursements
- Validate legalizations
- Return legalizations
- Validate refunds
- Register reimbursements

### ADMINISTRADOR

Can:

- Manage users
- Manage departments
- Manage approvers
- Manage expense types
- Manage cost centers
- Manage company accounts

---

## 10. Department Approval Rules

Every user belongs to a department.

Every department has configured approvers.

Approvers are explicitly assigned.

A National Manager cannot approve his own request.

National Managers are approved by the Country Director.

The system only supports approvals up to National Manager level.

---

## 11. Cost Allocation Rules

Every legalization must have exactly one cost allocation center.

Cost allocation centers can be:

### Department

Example:

DPT1234

### Operation Center

Example:

CO1234

The user selects from a predefined catalog.

Manual code entry is not allowed.

Inactive cost centers cannot be selected.

---

## 12. Legalization Rules

One request generates one legalization.

One legalization contains many expenses.

One legalization contains one support file package.

Support files are versioned.

Only the latest uploaded version is active.

Corrections update the same legalization.

No legalization history entity exists.

---

## 13. Financial Rules

One request generates one disbursement.

Disbursement amount may differ from requested amount.

Financial users must record justification for any adjustment.

Values are stored as integer amounts.

Example:

50000

125000

350000

No decimal values are used.

---

## 14. API Standards

### Base URL

/api/v1

### Resource Naming

Correct:

/users

/departments

/travel-requests

Incorrect:

/getUsers

/createDepartment

### HTTP Methods

GET

POST

PUT

PATCH

DELETE

### Response Wrapper

All responses must use:

ApiResponse<T>

---

## 15. DTO Standards

Requests:

- CreateUserRequest
- UpdateUserRequest

Responses:

- UserResponse
- UserSummaryResponse

Entities must never be exposed directly.

---

## 16. Mapper Standards

MapStruct only.

Mappers must:

- Convert DTO ↔ Entity

Mappers must not:

- Validate
- Query database
- Execute business logic

---

## 17. Database Standards

### Database

MySQL 8

### Naming Convention

Tables:

snake_case

Examples:

- users
- departments
- travel_requests

Columns:

snake_case

Examples:

- first_name
- created_at
- department_id

### Foreign Keys

Format:

entity_id

Examples:

- user_id
- department_id
- request_id

---

## 18. Testing Standards

### Unit Tests

JUnit 5

Mockito

### Coverage Targets

Service Layer

Business Rules

Validation Logic

---

## 19. Git Standards

Main Branch:

main

Commit Format:

feat:

fix:

refactor:

docs:

test:

chore:

Examples:

feat: create user module

fix: validate request status transition

refactor: simplify approval service

---

## 20. AI Agent Rules

Before generating code:

1. Read PROJECT_CONTEXT.md completely.
2. Respect all architectural decisions.
3. Do not modify business rules without explicit approval.
4. Do not introduce new frameworks.
5. Keep consistency with existing modules.
6. Follow SOLID and Clean Code principles.
7. Generate production-ready code.

---

## 21. Backend Package Structure

Base Package:

com.demo.travel_expense_management

com.demo.travel_expense_management
│
├── config
│ ├── database
│ ├── jackson
│ └── openapi
│
├── security
│ ├── config
│ ├── filter
│ ├── handler
│ ├── jwt
│ └── service
│
├── common
│ ├── constant
│ ├── dto
│ ├── exception
│ ├── response
│ └── util
│
├── user
├── department
├── account
├── travelrequest
├── approval
├── disbursement
├── legalization
├── refund
├── reimbursement
├── costcenter
├── expense
└── audit

---

### Module Internal Structure

Every business module must follow the same structure:

module
│
├── controller
├── dto
│ ├── request
│ └── response
├── entity
├── mapper
├── repository
├── service
└── exception

Example:

user
│
├── controller
├── dto
│ ├── request
│ └── response
├── entity
├── mapper
├── repository
├── service
└── exception

### Rules

- Package By Feature architecture is mandatory.
- Cross-module dependencies should be minimized.
- Controllers must only call services.
- Repositories must only be accessed from services.
- DTOs must be used for all API communication.
- Entities must never be exposed directly through controllers.
- Business logic belongs in services only.
- Mappers must only perform object transformations.
