# CAPSTONE

# GandhiMart

**GandhiMart** is a multi-seller e-commerce marketplace web application developed using Java Servlets, JDBC, Apache Tomcat, H2, HikariCP, JSP, vanilla JavaScript, and Maven.

**Developer:** Kishore Gandhi D
**Program:** B.E. CSE (Cyber Security)
**Institution:** J.J. College of Engineering and Technology, Trichy, Tamil Nadu, India

---

## Live Application

**Production URL:**
https://lively-generosity-production-3b55.up.railway.app

**Health Check:**
https://lively-generosity-production-3b55.up.railway.app/api/v1/health

---

## 1. Problem Statement

Many small marketplace systems provide only basic product browsing and purchasing functionality without clearly separating buyer, seller, and administrator responsibilities.

GandhiMart provides a centralized multi-seller marketplace where sellers can manage products, buyers can browse and purchase products, and administrators can manage users, orders, and listings.

The application is designed using a layered architecture to separate presentation, business logic, database access, and infrastructure responsibilities.

---

## 2. Objectives

* Develop a complete multi-seller e-commerce marketplace.
* Implement Buyer, Seller, and Admin roles.
* Provide secure authentication and session management.
* Allow sellers to manage product listings.
* Allow buyers to search, filter, cart, purchase, and review products.
* Implement database persistence using H2.
* Apply layered MVC architecture with DAO and Service layers.
* Implement automated testing and CI verification.
* Deploy the application publicly using Railway.

---

## 3. Features

| ID | Feature                           | Status                   |
| -- | --------------------------------- | ------------------------ |
| F1 | User registration and login       | ✅ Completed              |
| F2 | Seller product management         | ✅ Completed              |
| F3 | Product browsing/search/filtering | ✅ Completed              |
| F4 | Shopping cart                     | ✅ Completed              |
| F5 | Mock-payment checkout             | ✅ Completed              |
| F6 | Buyer/Seller order history        | ✅ Completed              |
| F7 | Admin management                  | ✅ Completed              |
| F8 | Product reviews and ratings       | ✅ Completed              |
| O1 | Wishlist                          | Optional                 |
| O2 | Order status workflow             | Optional                 |
| O3 | Seller sales dashboard            | Optional                 |
| O4 | AI chatbot                        | Final Review deliverable |

---

## 4. System Architecture

GandhiMart follows a **Layered MVC architecture over Java Servlets**.

```text
┌──────────────────────────────┐
│          Browser             │
│ HTML / CSS / JavaScript      │
│ fetch() / JSON               │
└──────────────┬───────────────┘
               │ HTTP Request
               ▼
┌──────────────────────────────┐
│       Filter Layer           │
│ Authentication / Security    │
└──────────────┬───────────────┘
               ▼
┌──────────────────────────────┐
│      Servlet / Controller    │
│ HTTP request + response      │
└──────────────┬───────────────┘
               ▼
┌──────────────────────────────┐
│        Service Layer         │
│ Validation + Business Logic  │
└──────────────┬───────────────┘
               ▼
┌──────────────────────────────┐
│          DAO Layer           │
│ JDBC + PreparedStatement     │
│                              │
└──────────────┬───────────────┘
               ▼
┌──────────────────────────────┐
│        HikariCP Pool         │
└──────────────┬───────────────┘
               ▼
┌──────────────────────────────┐
│          H2 Database         │
└──────────────────────────────┘
```

---

## 5. Design Diagrams

### D1 – Entity Relationship Diagram

[D1 ER Diagram] https://kommodo.ai/i/jIqcjfokmnmzC3ykDzUG

### D2 – Use Case Diagram

[D2 Use Case Diagram] https://kommodo.ai/i/gMrzTTPbLVmux9EzExka

### D3 – Place Order Sequence Diagram

[D3 Place Order Sequence Diagram] https://kommodo.ai/i/2iSA6gYZxsZFQB33sKJF

---

## 6. Technology Stack

| Component            | Technology                 |
| -------------------- | -------------------------- |
| Programming Language | Java 17                    |
| Web Framework        | Java Servlets              |
| Servlet Container    | Apache Tomcat 9            |
| Build Tool           | Maven                      |
| Database             | H2                         |
| Connection Pool      | HikariCP                   |
| Frontend             | JSP, HTML, CSS, JavaScript |
| API Communication    | Fetch API / JSON           |
| JSON Serialization   | Gson                       |
| Password Security    | jBCrypt / bcrypt           |
| Testing              | JUnit 5 + Mockito          |
| Static Analysis      | Checkstyle + SpotBugs      |
| CI/CD                | GitHub Actions             |
| Deployment           | Railway                    |

---

## 7. Project Structure

```text
GandhiMart/
│
├── src/
│   ├── main/
│   │   ├── java/com/gandhimart/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── dao/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── filter/
│   │   │   ├── listener/
│   │   │   ├── util/
│   │   │   └── exception/
│   │   │
│   │   ├── resources/
│   │   │   └── db/
│   │   │       └── migrations/
│   │   │
│   │   └── webapp/
│   │
│   └── test/
│
├── .github/
│   └── workflows/
│
├── docker/
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 8. Database

The application uses H2 as the relational database.

### Main Tables

* `users`
* `products`
* `orders`
* `order_items`
* `cart_items`
* `reviews`

The database uses primary keys, foreign keys, unique constraints, validation constraints, indexes, and migration scripts.

Database schema changes are maintained through numbered migration files.

---

## 9. Security

The following security mechanisms are implemented:

* Passwords are stored using bcrypt hashing.
* SQL operations use `PreparedStatement`.
* Protected API endpoints require an authenticated session.
* Session ID is regenerated after successful login.
* Explicit HTTP session timeout is configured.
* Generic custom error pages prevent stack-trace exposure.
* Database credentials are excluded from version control.
* User-generated content is handled through safe rendering techniques.
* Role-based access is enforced for Buyer, Seller, and Admin operations.

---

## 10. Testing

The project contains unit tests and DAO tests.

### Run tests

```bash
mvn clean test
```

### Run complete verification

```bash
mvn -B clean verify
```

The CI workflow runs the Maven verification process for repository changes.

---

## 11. Local Setup

### Requirements

* JDK 17
* Maven
* Apache Tomcat 9
* Git

### Clone repository

```bash
git clone https://github.com/gandhii1002-prog/CAPSTONE.git
cd CAPSTONE
```

### Build

```bash
mvn clean package
```

The WAR file will be generated in:

```text
target/gandhimart.war
```

### Deploy

Copy the WAR file to the Tomcat `webapps` directory.

Then start Tomcat.

Local application:

```text
http://localhost:8080/gandhimart/
```

---

## 12. API Overview

| Method | Endpoint                  | Purpose                     |
| ------ | ------------------------- | --------------------------- |
| POST   | `/api/v1/auth/register`   | Register Buyer/Seller       |
| POST   | `/api/v1/auth/login`      | Login                       |
| POST   | `/api/v1/auth/logout`     | Logout                      |
| GET    | `/api/v1/products`        | Browse/Search products      |
| POST   | `/api/v1/products/create` | Create product              |
| POST   | `/api/v1/products/update` | Update product              |
| POST   | `/api/v1/products/delete` | Delete product              |
| GET    | `/api/v1/cart`            | View cart                   |
| POST   | `/api/v1/cart/add`        | Add item                    |
| POST   | `/api/v1/cart/update`     | Update quantity             |
| POST   | `/api/v1/cart/remove`     | Remove item                 |
| POST   | `/api/v1/checkout`        | Place order                 |
| GET    | `/api/v1/orders/my`       | Buyer order history         |
| GET    | `/api/v1/orders/seller`   | Seller incoming orders      |
| GET    | `/api/v1/reviews`         | View reviews                |
| POST   | `/api/v1/reviews`         | Submit review               |
| GET    | `/api/v1/health`          | Application/database health |

---

## 13. Deployment

GandhiMart is packaged as a WAR and deployed using a Tomcat 9 Docker environment on Railway.

### Production URL

https://lively-generosity-production-3b55.up.railway.app

### Health response

```json
{
  "status": "ok",
  "db": "up"
}
```

---

## 14. Known Limitations / Pending Work

* Railway database persistence across a full restart/redeploy still requires final verification.
* Structured request-ID logging should be verified against the final codebase.
* AI chatbot integration is a Final Review deliverable.
* Final regression testing and presentation materials remain part of the final submission process.

---

## 15. Academic Context

This project is developed as a Java Semester 3 capstone project following the specified requirements for:

* Layered MVC architecture
* Java Servlets
* JDBC
* H2 database
* Authentication and session management
* Automated testing
* CI
* Cloud deployment
* Technical documentation

---

## 16. License

This project is developed for academic and educational purposes.
