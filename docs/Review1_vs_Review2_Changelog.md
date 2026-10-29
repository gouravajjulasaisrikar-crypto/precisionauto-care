# PrecisionAuto Care: Review 1 to Review 2 Progression & Technical Delta Report

**Course**: Service-Oriented Architecture (SOA) / Cloud-Native Microservices  
**Team**: KL University, 3rd-Year B.Tech CSE  
**Project**: PrecisionAuto Care — Automotive Fleet Maintenance & Service Operations Platform  

---

## Executive Summary of Advancements (Review 1 vs. Review 2)

| Dimension | Review 1 (Baseline / Foundation) | Review 2 (Advanced Implementation) | Technical Impact & Evidence |
| :--- | :--- | :--- | :--- |
| **1. Inter-Service Communication** | Isolated service silos; mock returns; no inter-process network calls. | **Synchronous RPC via Spring Cloud OpenFeign** (`RecordClient`, `BillingClient`). | Completing a job in `booking-service` triggers automated pipeline across 3 microservices simultaneously. |
| **2. Security & Zero-Trust** | Unauthenticated direct access; plaintext passwords; no token gateway. | **Stateless HMAC-SHA256 JWT + BCrypt + Two-Factor Authentication (OTP 2FA) + Gateway Filter** (`JwtAuthenticationFilter`, `AuthService.generateOtp`). | 2-step verification: credentials + 6-digit OTP token. API Gateway (8080) validates tokens and enriches downstream requests. |
| **3. Bay Conflict Elimination** | Simple calendar entries; no collision checks; double-booking possible. | **Automated Collision Elimination Engine** (`HTTP 409 Conflict`). | Spring Data JPA derived queries validate bay occupancy (`BAY-1`, `BAY-2`, `BAY-3`). Prevents duplicate bay scheduling. |
| **4. Invoicing & Billing** | Static pre-seeded bills; no dynamic tax; manual calculation. | **Dynamic Tax Calculation & Payment Lifecycle** (`PENDING` &rarr; `PAID`). | Real-time computation: `Amount = (Labor + Parts) + 10% Tax`. One-click "Pay Now" payment settlement. |
| **5. Frontend UI & UX** | Basic layout; no role restrictions; generic styling. | **Enterprise High-Tech SaaS Telemetry Dashboard with Strict RBAC**. | Ultra-modern geometric typography (`Plus Jakarta Sans`, `Inter`, `JetBrains Mono`), dark titanium glassmorphism, glowing telemetry badges, SVG vector icons, cyber CAPTCHA. |
| **6. Role Separation (RBAC)** | All users saw identical buttons and views. | **Strict Persona Separation (Customer vs Tech vs Admin)**. | Customer cannot complete repairs; Tech cannot view financial billing; Admin has full superuser governance. |
| **7. Startup & Orchestration** | Manual command prompts needed for each service. | **1-Click Master Review Startup** (`START_PROJECT_FOR_REVIEW.bat`). | Single automated batch script orchestrates Eureka, Gateway, 4 microservices, and Node frontend in sequence. |

---

## 🎯 Detailed Demonstration Script for Review 2 (5-Minute Viva Flow)

### Step 1: Show the Architectural Delta (Eureka Registry & Gateway)
* **What to Show**: Open `http://localhost:8761` (Eureka Dashboard).
* **What to Say**:
  > *"In Review 1, our services were standalone and did not discover each other. For Review 2, we have registered all 5 microservices (`API-GATEWAY`, `AUTH-SERVICE`, `BOOKING-SERVICE`, `RECORD-SERVICE`, `BILLING-SERVICE`) dynamically with Netflix Eureka on port 8761. The API Gateway on port 8080 now load-balances traffic using `lb://` service discovery with zero hardcoded IPs."*

### Step 2: Show the Zero-Trust Security & CAPTCHA (Review 2 Addition)
* **What to Show**: Open `http://localhost:3000` (Login Screen).
* **What to Say**:
  > *"In Review 1, we had no security gateway. In Review 2, we implemented an institutional Zero-Trust security portal. Passwords are encrypted with BCrypt (cost factor 12). Login requests generate a signed HMAC-SHA256 JWT token. We also added an anti-bot canvas CAPTCHA and an exponential brute-force lockout guard."*

### Step 3: Demonstrate the Collision Elimination Engine (Review 2 Feature)
* **What to Show**: Sign in as **Customer** (`customer` / `cust123`). Click **"Simulate Bay 1 Collision"**.
* **What to Say**:
  > *"A major deliverable for Review 2 is solving bay scheduling conflicts. When I click this button, the customer attempts to book `BAY-1` at an already occupied time slot. The Booking Service queries existing reservations using Spring Data JPA and immediately blocks the reservation, returning an explicit **HTTP 409 Conflict** error."*

### Step 4: Demonstrate OpenFeign Inter-Service Orchestration (The Core SOA Highlight!)
* **What to Show**:
  1. Sign in as **Technician** (`technician` / `tech123`).
  2. Click **"Complete Job"** on an active vehicle booking.
  3. Enter Labor Charge (`$150.00`) and Parts Charge (`$180.00`).
  4. Point out the dynamic 10% tax calculation preview (`Subtotal $330.00 + Tax $33.00 = Total $363.00`).
  5. Click **"Finalize Service & Trigger Invoicing"**.
* **What to Say**:
  > *"This is the core SOA achievement of Review 2. Notice what happened with a single click:  
  > 1. `booking-service` (8082) updated the booking status to `COMPLETED`.  
  > 2. It executed an **OpenFeign RPC call** to `record-service` (8083) to create a permanent diagnostic repair record.  
  > 3. It executed a second **OpenFeign RPC call** to `billing-service` (8084) to generate a dynamic invoice with 10% tax.  
  > Three independent microservices synchronized seamlessly without shared databases."*

### Step 5: Settle Invoice & Review RBAC (Customer Persona Switch)
* **What to Show**:
  1. Click **"Sign Out"** (point out session revocation preserving data).
  2. Sign in as **Customer** (`customer` / `cust123`).
  3. Click **"My Invoices & Payments"**.
  4. Point to the newly generated invoice (`PENDING`).
  5. Click **"Pay Now"** &rarr; Badge instantly switches to `PAID / Settled`.
* **What to Say**:
  > *"The newly generated invoice appears instantly in the customer's billing ledger. Notice the Role-Based Access Control: the Customer can click 'Pay Now' to settle the bill, but cannot access technician diagnostic inputs or service bay queue controls."*

---

## 🛠️ Code Reference Map for Review 2 Deliverables

| Review 2 Deliverable | File Path | Method / Code Lines to Point to |
| :--- | :--- | :--- |
| **OpenFeign Orchestration** | `booking-service/.../BookingService.java` | `completeBooking()` lines 109–165 (`recordClient.createRecord()` & `billingClient.generateBill()`) |
| **OpenFeign Interfaces** | `booking-service/.../client/` | `BillingClient.java` (`@FeignClient(name = "billing-service")`) & `RecordClient.java` |
| **Collision Engine (409)** | `booking-service/.../BookingService.java` | `scheduleBooking()` lines 38–67 (`findByBayIdAndServiceDateAndStatusNot()`) |
| **Controller HTTP 409 Mapping** | `booking-service/.../BookingController.java` | `scheduleBooking()` lines 27–38 (`catch (IllegalStateException e) -> 409 CONFLICT`) |
| **Dynamic Invoicing (10% Tax)** | `billing-service/.../BillingService.java` | `generateBill()` lines 20–39 (`tax = subtotal * 0.10; amount = subtotal + tax`) |
| **Gateway JWT Filter** | `api-gateway/.../JwtAuthenticationFilter.java` | `apply()` lines 34–80 (Validates Bearer token, mutates `X-User-Id` & `X-User-Role` headers) |
| **BCrypt Password Hashing** | `auth-service/.../AuthService.java` | `authenticate()` & `register()` using `PasswordEncoder.matches()` |
| **1-Click Review Startup** | `START_PROJECT_FOR_REVIEW.bat` | Orchestrates Eureka &rarr; Auth &rarr; Record &rarr; Billing &rarr; Booking &rarr; Gateway &rarr; Frontend |
