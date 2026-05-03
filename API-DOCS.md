# Ecommerce Saga — API Documentation

## Swagger UI Endpoints

Each microservice exposes its own OpenAPI 3 / Swagger UI:

| Service | Swagger UI | OpenAPI Spec |
|---------|------------|--------------|
| User Service | http://localhost:8766/swagger-ui.html | http://localhost:8766/v3/api-docs |
| Product Service | http://localhost:8767/swagger-ui.html | http://localhost:8767/v3/api-docs |
| Stock Service | http://localhost:8768/swagger-ui.html | http://localhost:8768/v3/api-docs |
| Order Service | http://localhost:8769/swagger-ui.html | http://localhost:8769/v3/api-docs |
| Payment Service | http://localhost:8770/swagger-ui.html | http://localhost:8770/v3/api-docs |

## Quick start with Swagger UI

1. Open User Service Swagger UI: http://localhost:8766/swagger-ui.html
2. Try POST /api/user/signin with admin credentials
3. Copy the `token` field from the response
4. Click the "Authorize" button (top right, lock icon)
5. Paste the token (without "Bearer " prefix) and click Authorize
6. Now you can call protected endpoints directly from Swagger UI

## Architecture

- Gateway: http://localhost:8763 — JWT validation, route forwarding
- Eureka: http://localhost:8761 — service discovery dashboard
- RabbitMQ Management: http://localhost:15672 (guest/guest)
- Iyzico Sandbox: https://sandbox-api.iyzipay.com (configured in payment-service)

## Saga Flow (POST /api/order)

1. order-service creates Order (status=PENDING, sagaStatus=STARTED)
2. → reserveStockStep: Feign call to stock-service
3. stock-service: decrement product-service stock + create reservation (PENDING)
4. → publishPaymentRequest: RabbitMQ → payment.request.queue
5. payment-service: consume → Iyzico API → publish to payment.response.queue
6. order-service: consume response
   - SUCCESS: confirm stock, order CONFIRMED
   - FAILURE: compensate (cancel stock = restore product stock), order CANCELLED
