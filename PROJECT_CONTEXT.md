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

### Flow Diagram

```
BORRADOR
  │
  ├── (submitForApproval) ──→ ENVIADA
  │
  │   ENVIADA
  │     │
  │     ├── (approve) ──────→ APROBADA
  │     ├── (return) ───────→ DEVUELTA
  │     └── (reject) ───────→ RECHAZADA
  │
  │   DEVUELTA
  │     │
  │     └── (resubmit) ─────→ ENVIADA
  │
  │   APROBADA
  │     │
  │     └── (disburse) ─────→ DESEMBOLSADA
  │
  │   DESEMBOLSADA
  │     │
  │     └── (legalize) ─────→ LEGALIZADA
  │
  │   LEGALIZADA
  │     │
  │     ├── (validate) ─────→ VALIDADA
  │     └── (return) ───────→ LEGALIZACION_DEVUELTA
  │
  │   LEGALIZACION_DEVUELTA
  │     │
  │     └── (resubmit) ─────→ LEGALIZADA
  │
  │   VALIDADA
  │     │
  │     ├── (closeBalanced) ────────────────→ CERRADA
  │     ├── (markPendingRefund) ────────────→ PENDIENTE_DEVOLUCION
  │     └── (markPendingReimbursement) ────→ PENDIENTE_REEMBOLSO
  │
  │   PENDIENTE_DEVOLUCION
  │     │
  │     └── (registerRefund) ──────────────→ CERRADA
  │
  │   PENDIENTE_REEMBOLSO
  │     │
  │     └── (registerReimbursement) ───────→ CERRADA
  │
  RECHAZADA, CERRADA — terminal states
```

### State Transition Matrix

| Current Status | Next Status | Trigger | Responsible Role | Side Effect |
|---|---|---|---|---|
| BORRADOR | ENVIADA | submitForApproval | EMPLEADO | Validation, requestNumber generation |
| BORRADOR | — | updateDraft | EMPLEADO | No transition (stays BORRADOR) |
| ENVIADA | APROBADA | approve | APROBADOR | Creates Approval record (APROBADA) |
| ENVIADA | DEVUELTA | returnForCorrection | APROBADOR | Creates Approval record (DEVUELTA) |
| ENVIADA | RECHAZADA | reject | APROBADOR | Creates Approval record (RECHAZADA) |
| DEVUELTA | ENVIADA | resubmit | EMPLEADO | Clears previous approvals |
| APROBADA | DESEMBOLSADA | registerDisbursement | FINANCIERA | Creates Disbursement record |
| DESEMBOLSADA | LEGALIZADA | submitLegalization | EMPLEADO | Creates Legalization with Expenses + SupportFiles |
| LEGALIZADA | VALIDADA | validateLegalization | FINANCIERA | Sets validatedAt on Legalization |
| LEGALIZADA | LEGALIZACION_DEVUELTA | returnLegalization | FINANCIERA | Sets observations on Legalization |
| LEGALIZACION_DEVUELTA | LEGALIZADA | resubmitLegalization | EMPLEADO | New versions of support files |
| VALIDADA | CERRADA | closeWhenBalanced | FINANCIERA | disbursedAmount == totalExpensed |
| VALIDADA | PENDIENTE_DEVOLUCION | markPendingRefund | FINANCIERA | disbursedAmount > totalExpensed |
| VALIDADA | PENDIENTE_REEMBOLSO | markPendingReimbursement | FINANCIERA | disbursedAmount < totalExpensed |
| PENDIENTE_DEVOLUCION | CERRADA | registerRefund | FINANCIERA | Creates Refund record |
| PENDIENTE_REEMBOLSO | CERRADA | registerReimbursement | FINANCIERA | Creates Reimbursement record |

### Forbidden Transitions

| From | To | Reason |
|---|---|---|
| Any → BORRADOR | Irreversible submission |
| RECHAZADA → Any | Terminal state |
| CERRADA → Any | Terminal state |
| DESEMBOLSADA → DEVUELTA | After disbursement, corrections go through legalization |
| BORRADOR → APROBADA | Must be submitted first |
| APROBADA → ENVIADA | Cannot un-submit after approval |
| Any → DESEMBOLSADA | Can only come from APROBADA |

### Operation Matrix

| Operation | Initial Status | Final Status | Authorized Roles | Preconditions |
|---|---|---|---|---|
| createDraft | — | BORRADOR | EMPLEADO | User must be active |
| updateDraft | BORRADOR | BORRADOR | EMPLEADO | User is the applicant |
| submitForApproval | BORRADOR | ENVIADA | EMPLEADO | All required fields filled |
| approve | ENVIADA | APROBADA | APROBADOR | Approver assigned to department |
| returnForCorrection | ENVIADA | DEVUELTA | APROBADOR | Comments required |
| reject | ENVIADA | RECHAZADA | APROBADOR | Comments required |
| resubmit | DEVUELTA | ENVIADA | EMPLEADO | Corrections applied |
| registerDisbursement | APROBADA | DESEMBOLSADA | FINANCIERA | Disbursement does not exist yet |
| submitLegalization | DESEMBOLSADA | LEGALIZADA | EMPLEADO | Legalization does not exist yet; expenses + support files included |
| validateLegalization | LEGALIZADA | VALIDADA | FINANCIERA | Expenses verified; support files reviewed |
| returnLegalization | LEGALIZADA | LEGALIZACION_DEVUELTA | FINANCIERA | Observations required |
| resubmitLegalization | LEGALIZACION_DEVUELTA | LEGALIZADA | EMPLEADO | Corrections applied |
| closeWhenBalanced | VALIDADA | CERRADA | FINANCIERA | disbursedAmount == totalExpensed |
| markPendingRefund | VALIDADA | PENDIENTE_DEVOLUCION | FINANCIERA | disbursedAmount > totalExpensed |
| markPendingReimbursement | VALIDADA | PENDIENTE_REEMBOLSO | FINANCIERA | disbursedAmount < totalExpensed |
| registerRefund | PENDIENTE_DEVOLUCION | CERRADA | FINANCIERA | Refund record created |
| registerReimbursement | PENDIENTE_REEMBOLSO | CERRADA | FINANCIERA | Reimbursement record created |

### Role Permissions By State

| State | EMPLEADO | APROBADOR | FINANCIERA | ADMINISTRADOR |
|---|---|---|---|---|
| BORRADOR | create, update, submit | — | — | — |
| ENVIADA | — | approve, return, reject | — | — |
| DEVUELTA | resubmit | — | — | — |
| APROBADA | — | — | disburse | — |
| RECHAZADA | read | — | — | — |
| DESEMBOLSADA | legalize | — | — | — |
| LEGALIZADA | — | — | validate, return | — |
| LEGALIZACION_DEVUELTA | resubmit | — | — | — |
| VALIDADA | — | — | close, markRefund, markReimbursement | — |
| PENDIENTE_DEVOLUCION | — | — | registerRefund | — |
| PENDIENTE_REEMBOLSO | — | — | registerReimbursement | — |
| CERRADA | read | — | — | — |

### Conceptual Status Mapping

The following conceptual statuses used in business discussions map to concrete enum values:

| Conceptual | Enum Value | Context |
|---|---|---|
| PENDIENTE_APROBACION | ENVIADA | Request submitted, awaiting approver decision |
| REQUIERE_CORRECCION | DEVUELTA | Request-level correction (pre-disbursement) |
| REQUIERE_CORRECCION | LEGALIZACION_DEVUELTA | Legalization-level correction (post-disbursement) |

---

## 7. Enumerations

### RequestStatus

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

### ApprovalDecision

- APROBADA
- DEVUELTA
- RECHAZADA

### AccountType

- AHORROS
- CORRIENTE

### CostCenterType

- DEPARTMENT
- OPERATION_CENTER

---

## 8. Domain Modules

### Security

Authentication and Authorization

### User

User management

### Department

Department management: organizational units that group users, configure approvers, and serve as cost allocation centers.

### Account

User bank accounts. Each user may have multiple accounts (savings or checking).

Account/entity contains:
• Account
• AccountType

### TravelRequest

Travel advance requests: employee-submitted requests that flow through approval, disbursement, legalization, and closure.

TravelRequest has a unique requestNumber (business key) generated by the system.

Request number format: TRV-YYYY-NNNNNN (e.g., TRV-2026-000001). Year is the current year, sequence is auto-incremented.

### TravelRequest Endpoints

| Endpoint | Method | Description |
|---|---|---|
| /api/v1/travel-requests/draft | POST | Create draft (status BORRADOR) |
| /api/v1/travel-requests/{id}/draft | PUT | Update draft (only BORRADOR, only owner) |
| /api/v1/travel-requests/{id}/submit | POST | Submit for approval (BORRADOR → ENVIADA) |
| /api/v1/travel-requests/{id}/cancel | POST | Cancel draft (BORRADOR → RECHAZADA) |
| /api/v1/travel-requests/{id} | GET | Find by ID |
| /api/v1/travel-requests | GET | Paginated list (all) |
| /api/v1/travel-requests/my | GET | Paginated list (current user only) |

Submit requires the applicant's department to have at least one active approver.

### Approval

Approval process

### Approval Endpoints

| Endpoint | Method | Description |
|---|---|---|
| /api/v1/approvals/travel-request/{id}/approve | POST | Approve request (ENVIADA → APROBADA) |
| /api/v1/approvals/travel-request/{id}/reject | POST | Reject request (ENVIADA → RECHAZADA) |
| /api/v1/approvals/travel-request/{id}/history | GET | Approval history for a request |

Approve sets `approvedAmount` on the TravelRequest. Reject requires comments.
Both require the logged user to be an active approver for the applicant's department.
A user can only act once per request.

### Disbursement

Travel advance payments

### Disbursement Endpoints

| Endpoint | Method | Description |
|---|---|---|
| /api/v1/disbursements/travel-request/{id} | POST | Register disbursement (APROBADA → DESEMBOLSADA) |
| /api/v1/disbursements/travel-request/{id} | GET | Find disbursement by travel request |
| /api/v1/disbursements | GET | Paginated list of disbursements |

Register requires APROBADA status, no existing disbursement, and role FINANCIERA or ADMINISTRADOR. If disbursedAmount ≠ approvedAmount, adjustmentJustification is mandatory.

### Legalization

Expense reporting: contains expenses, support files, and a cost center allocation. Connected to a single TravelRequest.

Legalization entity does NOT have its own `status` field — status is derived from the associated `TravelRequest.RequestStatus` (LEGALIZADA, LEGALIZACION_DEVUELTA, VALIDADA).

Legalization/entity contains:
• Legalization
• SupportFile

### SupportFile

Versioned file attachments for legalizations. Files are stored on the local filesystem; metadata in the database.

SupportFile entity fields:
- legalization (ManyToOne → Legalization)
- versionNumber
- originalFileName / storedFileName / filePath
- mimeType / fileSize
- active (boolean)
- uploadedAt

One Legalization contains many SupportFile records. SupportFiles are versioned — only the latest active version is visible.

DTO fields:

SupportFileResponse:
- id, legalizationId, originalFileName, storedFileName, mimeType, fileSize, version (maps from versionNumber), active, uploadedAt

SupportFileSummaryResponse:
- id, originalFileName, version, uploadedAt

### Legalization Endpoints

| Endpoint | Method | Description |
|---|---|---|
| /api/v1/legalizations | POST | Submit legalization (DESEMBOLSADA → LEGALIZADA) |
| /api/v1/legalizations/{id} | GET | Find by ID |
| /api/v1/legalizations | GET | Paginated list |
| /api/v1/legalizations/travel-request/{id} | GET | Find by travel request |
| /api/v1/legalizations/{id}/validate | POST | Validate (LEGALIZADA → VALIDADA) |
| /api/v1/legalizations/{id}/return | POST | Return for correction (LEGALIZADA → LEGALIZACION_DEVUELTA) |
| /api/v1/legalizations/{id}/resubmit | POST | Resubmit corrected (LEGALIZACION_DEVUELTA → LEGALIZADA) |

### Expense

Expense records: individual expense items grouped under a legalization. Each expense references an ExpenseType catalog entry.

Expense/entity contains:
• Expense
• ExpenseType

### CostCenter

Accounting allocation: selected from a predefined catalog during legalization creation.

### Refund

Employee refund to company: created when the employee spent less than the disbursed amount. One Legalization may have at most one Refund.

Refund/entity contains:
• Refund

### Reimbursement

Company reimbursement to employee: created when the employee spent more than the disbursed amount. One Legalization may have at most one Reimbursement.

Reimbursement/entity contains:
• Reimbursement

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

Approvers are explicitly assigned through a DepartmentApprover configuration entity.

A department may have one or more approvers.

Only active approver assignments are considered during approval routing.

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

### Core Entities

One request generates one Legalization.

One Legalization contains many Expense records.

One Legalization has exactly one CostCenter (FK cost_center_id).

One Legalization contains many SupportFile records (versioned).

### Legalization

Legalization entity fields:

- travelRequest (OneToOne, unique FK)
- costCenter (ManyToOne, required)
- submittedBy (ManyToOne → User)
- submittedAt
- validatedAt (nullable, set by FINANCIERA)
- observations (nullable, TEXT)
- expenses (OneToMany → Expense, CascadeType.ALL, orphanRemoval = true)
- supportFiles (OneToMany → SupportFile, CascadeType.ALL, orphanRemoval = true)
- createdAt, updatedAt (timestamps)

**IMPORTANT:** The Legalization entity does NOT have a `status` field. Status is managed at the TravelRequest aggregate level via `TravelRequest.RequestStatus`. Legalization lifecycle statuses are `LEGALIZADA`, `LEGALIZACION_DEVUELTA`, `VALIDADA` — all stored as `TravelRequest.status`. The DTO `status` field is derived from `legalization.travelRequest.status` in the mapper.

Relationship: TravelRequest 1 ↔ 1 Legalization

### Expense

Expense entity fields:

- legalization (ManyToOne → Legalization)
- expenseType (ManyToOne → ExpenseType catalog)
- expenseDate
- description
- amount (integer)
- createdAt, updatedAt (timestamps)

One Legalization contains many Expenses. Expenses are lifecycle-managed by the parent Legalization (cascade persist, cascade delete). The embedded `expenses` list on Legalization uses `orphanRemoval = true`.

### DTO Fields

LegalizationResponse:
- id, travelRequestId, requestNumber, applicantName, costCenterId, costCenterName
- **totalExpenses**: computed field = sum of all Expense.amount in the legalization (not persisted)
- **status**: derived from travelRequest.status (LEGALIZADA / LEGALIZACION_DEVUELTA / VALIDADA)
- **submittedAt**: from entity field
- **expenses**: List&lt;ExpenseResponse&gt; (flattened)

LegalizationSummaryResponse:
- id, requestNumber, applicantName, totalExpenses (computed), status (derived)

ExpenseResponse:
- id, expenseTypeId, expenseTypeName, expenseDate, description, amount

### ExpenseType

ExpenseType is a catalog entity (not an enum):

- code (unique)
- name (unique)
- active (boolean)

Managed via admin CRUD by ADMINISTRADOR role.

### SupportFile

SupportFile entity fields:

- legalization (ManyToOne)
- versionNumber
- originalFileName / storedFileName / filePath
- mimeType / fileSize
- active (boolean)
- uploadedAt

Support files are versioned. Only the latest uploaded version is active.

When corrections are required, new files are uploaded with versionNumber + 1 and previous files are marked active = false.

### Correction Process

Corrections update the same legalization. No legalization history entity exists.

When FINANCIERA returns a legalization:

1. TravelRequest status → LEGALIZACION_DEVUELTA
2. Employee edits expenses on the existing Legalization
3. Employee uploads new support file versions
4. Employee resubmits → status back to LEGALIZADA
5. FINANCIERA reviews again

### Financial Validation

When FINANCIERA validates a legalization:

1. TravelRequest status → VALIDADA
2. validatedAt is set on the Legalization record
3. System calculates the closure path:

   | Condition | Result |
   |-----------|--------|
   | disbursedAmount == totalExpensed | CERRADA |
   | disbursedAmount > totalExpensed | PENDIENTE_DEVOLUCION |
   | disbursedAmount < totalExpensed | PENDIENTE_REEMBOLSO |

When FINANCIERA returns a legalization:

1. TravelRequest status → LEGALIZACION_DEVUELTA
2. observations may contain the return reason

---

## 13. Financial Rules

One request generates one disbursement.

Disbursement amount may differ from requested amount.

Financial users must record justification for any adjustment.

Each Disbursement references exactly one TravelRequest through request_id.

Relationship:

TravelRequest 1 ↔ 1 Disbursement

Disbursement contains an adjustmentJustification field used when the approved disbursement amount differs from the requested amount.

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

Fields: success, message, data, timestamp (LocalDateTime)

Factory methods:

- success(T data) — message is null
- success(String message, T data)
- error(String message)

Standard HTTP status patterns:

- 200 OK: Read, update
- 201 Created: Create
- 204 No Content: Delete
- 400 Bad Request: Business error, validation error
- 401 Unauthorized: Authentication required
- 403 Forbidden: Insufficient permissions
- 404 Not Found: Resource not found
- 500 Internal Server Error: Unexpected server error

### Pagination

Pagination uses Spring Page<T> inside ApiResponse.

Query parameters:

- page: zero-based page index (default 0)
- size: page size (default 20)
- sort: property, direction (e.g., sort=id,asc)

Example:

GET /users?page=0&size=20&sort=lastName,asc

---

## 15. DTO Standards

### Naming Convention

Requests (input):

- Create{Entity}Request
- Update{Entity}Request

Responses (output):

- {Entity}Response (detail view)
- {Entity}SummaryResponse (list view)

Examples:

- CreateUserRequest → UserResponse / UserSummaryResponse
- CreateTravelRequestRequest → TravelRequestResponse / TravelRequestSummaryResponse

### Rules

Entities must never be exposed directly.

Use Java records for DTOs. Use Jakarta Validation annotations on request records.

DTOs must not contain business logic.

DTOs must reference entities by ID, never by entity object.

---

## 16. Mapper Standards

### Framework

MapStruct only.

Annotation: @Mapper(componentModel = "spring", uses = {OtherMapper.class})

### Responsibilities

- Convert DTO → Entity
- Convert Entity → DTO

### Forbidden

- Validate
- Query database
- Execute business logic

### Naming

Mapper interface: {Entity}Mapper

Standard methods:

- {Entity} toEntity({CreateRequest} request)
- {Entity} toEntity({UpdateRequest} request)
- {Response} toResponse({Entity} entity)
- {SummaryResponse} toSummaryResponse({Entity} entity)
- List{SummaryResponse} toSummaryResponseList(List{Entity} entities)

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
│
│   department/entity contains:
│   • Department
│   • DepartmentApprover
│
│   DepartmentApprover represents the assignment of approvers to
│   departments and is used by the approval routing process.
│
│   DepartmentApprover entity fields:
│   • department (ManyToOne, required) — the department being configured
│   • approver (ManyToOne → User, required) — the user assigned as approver
│   • active (boolean)
│
│   Naming convention:
│   • Entity field: approver — reflects the business role (approval routing)
│   • API DTOs: userId, userFullName, userEmail — user-friendly API contract
│   • Repository: findByDepartmentIdAndApproverId matches entity field name
│
├── account
├── travelrequest
├── approval
├── disbursement
├── legalization
│
│   legalization/entity contains:
│   • Legalization
│   • SupportFile
│
│   Legalization aggregates expenses and support files for a
│   single travel request. SupportFile tracks versioned file uploads.
│
├── refund
├── reimbursement
├── costcenter
├── expense
│
│   expense/entity contains:
│   • Expense
│   • ExpenseType
│
│   ExpenseType is a catalog entity managed by ADMINISTRADOR.
│
├── storage
│   │   (cross-cutting infrastructure — no entity, no controller)
│   │
│   └── service
│       ├── FileStorageService.java        (interface)
│       └── LocalFileStorageService.java   (@Service implementation)
│
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

---

## 22. File Storage Strategy

### Service Contract

File operations are abstracted behind `FileStorageService` interface in `storage/service/`:

| Method | Signature | Returns |
|---|---|---|
| store | `(MultipartFile file, Long legalizationId, Integer version)` | `String` — UUID-based stored file name |
| load | `(String storedFileName)` | `Resource` — readable file resource |
| delete | `(String storedFileName)` | `void` |
| exists | `(String storedFileName)` | `boolean` |

The `load/delete/exists` methods receive the **file path** stored in the entity's `filePath` field (e.g., `42/1/uuid.pdf`), which the implementation resolves relative to `storage.location`.

### MVP Implementation: LocalFileStorageService

- `@Service`, reads `storage.location` via `@Value("${storage.location}")`.
- Path structure: `{storage.location}/{legalizationId}/{version}/{storedFileName}`.
- Directories created automatically via `Files.createDirectories()`.
- File names are UUID-based to avoid collisions. Original extension preserved.
- Returns only the stored file name (UUID). The caller constructs the `filePath` for the entity.

### Configuration

```yaml
storage:
  location: ${STORAGE_LOCATION:uploads/support-files}

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

### Technical Validation

MIME type (`application/pdf`, `image/jpeg`, `image/png`) and file size (10 MB max) are enforced by the **legalization service caller**, not by the storage service. The storage service performs no business validations.

### Versioning

- On initial upload: versionNumber = 1, active = true.
- On correction upload: previous files set to active = false, new files get versionNumber + 1, active = true.
- Queries filter by `active = true` to retrieve the latest package.
- Previous version files remain on disk; cleanup is handled by a scheduled job if needed.

### Repository Queries

SupportFileRepository (in `legalization/repository/`):

| Method | Purpose |
|---|---|
| `findByLegalizationIdAndActiveTrue(Long legalizationId)` | Active files for a legalization |
| `findByLegalizationIdOrderByVersionNumberDesc(Long legalizationId)` | All files ordered by version (newest first) |
| `findTopByLegalizationIdOrderByVersionNumberDesc(Long legalizationId)` | Latest version number for a legalization |

Entity field is `versionNumber` (not `version`), so JPA derived query methods must use `VersionNumber` in the method name.

### Production Path

- Swap local filesystem for S3-compatible object storage.
- Replace file writes with cloud SDK calls behind the existing `FileStorageService` interface.
- Add virus scanning, CDN delivery, and pre-signed URLs for secure access.

---

## 23. Transaction Management

### Scope

Transactional boundaries are at the Service layer only.

### Annotations

- @Transactional(rollbackFor = Exception.class) on write operations
- @Transactional(readOnly = true) on query operations
- @Transactional(readOnly = true) at class level with method-level override for writes

### Rules

- Rollback on any Exception (not just RuntimeException).
- Propagation: REQUIRED (default) — join existing or create new.
- Isolation: READ_COMMITTED (default).

### Forbidden

- @Transactional on controllers.
- @Transactional on repositories.
- Manual EntityManager.flush() or commit() in business code.

---

## 24. Validation Strategy

### Framework

Jakarta Validation on request DTOs.

### Location

Annotations are placed on request DTO record fields.

### Standard Annotations

- @NotBlank — required text
- @NotNull — required numeric/enum
- @Positive — amounts
- @Email — email format
- @Size(max = ...) — length limits
- @PastOrPresent — dates

### Cross-field Validation

Validated in the Service layer, not in DTOs.

### Groups

Use marker interfaces (Create.class, Update.class) when create and update validation differs.

### Forbidden

- Validation inside entities.
- Validation inside mappers.
- Database queries inside custom validators.

---

## 25. Pagination and Filtering

### Pagination

Use Spring Page<T> inside ApiResponse.

Parameters:

- page: zero-based (default 0)
- size: items per page (default 20)
- sort: property, direction (e.g., sort=id,asc)

### Filtering

Use Spring Data JPA Specifications for dynamic query composition.

Specification classes are placed in {module}/repository/.

Simple filters use Spring Data derived query methods in the repository interface.

### Repository Base Interfaces

Each module repository extends:

- JpaRepository{Entity, Long}
- JpaSpecificationExecutor{Entity} (when dynamic filtering is needed)
