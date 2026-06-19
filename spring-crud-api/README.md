# Spring CRUD REST API

A compact Spring Boot project intended to exercise Java-to-Python conversion tools.
It contains a conventional layered REST API, JPA persistence, validation, exception
handling, DTO records, an enum, mapping code, streams, `Optional`, and automated tests.

## Requirements

- Java 17 or newer
- Maven 3.6.3 or newer, or the included Maven Wrapper

## Run

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080/api/products`.
The in-memory H2 console is available at `http://localhost:8080/h2-console`.

H2 connection values:

- JDBC URL: `jdbc:h2:mem:catalogdb`
- User: `sa`
- Password: empty

## Test

```powershell
.\mvnw.cmd test
```

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `POST` | `/api/products` | Create a product |
| `GET` | `/api/products` | List products; accepts `status` and `query` |
| `GET` | `/api/products/{id}` | Fetch one product |
| `PUT` | `/api/products/{id}` | Replace editable product fields |
| `PATCH` | `/api/products/{id}/status` | Change product status |
| `DELETE` | `/api/products/{id}` | Delete a product |

Example create request:

```json
{
  "sku": "KB-100",
  "name": "Mechanical Keyboard",
  "description": "Compact keyboard with tactile switches",
  "price": 89.99,
  "stockQuantity": 25,
  "status": "ACTIVE"
}
```

Example filters:

```text
GET /api/products?status=ACTIVE
GET /api/products?query=keyboard
GET /api/products?status=ACTIVE&query=keyboard
```

