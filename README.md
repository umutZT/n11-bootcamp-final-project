# 🛒 n11 — E-Commerce Microservices (Bootcamp Bitirme Projesi)

Mikroservis mimarisi ile geliştirilmiş, **Saga Orchestration Pattern** uygulayan e-ticaret sistemi. JWT auth, RabbitMQ mesajlaşma, Iyzico ödeme entegrasyonu, React frontend + Spring Boot backend.

## 🎯 Proje Özellikleri

### Backend
- ✅ **5 İşlevsel Mikroservis** + 3 altyapı servisi (Discovery, Config, Gateway)
- ✅ **Saga Orchestration Pattern** (order-service merkez orchestrator)
- ✅ **Compensation Pattern** (ödeme başarısızsa stok rollback)
- ✅ **JWT Authentication & Authorization** (HS256, gateway'de doğrulama)
- ✅ **Iyzico Sandbox** ödeme entegrasyonu (gerçek API)
- ✅ **Pagination + Filtering + Search** (ürünler için)
- ✅ **OpenFeign** (servisler arası senkron çağrı)
- ✅ **RabbitMQ** (asenkron mesajlaşma, payment akışı)
- ✅ **Spring Cloud Config** (centralized config server)
- ✅ **Eureka** (service discovery)
- ✅ **API Gateway** (routing + JWT validation + CORS)
- ✅ **PostgreSQL** (her servise ayrı DB - DB-per-service prensibi)
- ✅ **Swagger/OpenAPI 3** (her servise dokümantasyon)
- ✅ **JUnit 5 + Mockito** (saga compensation odaklı testler)
- ✅ **JaCoCo** test coverage raporu
- ✅ **Docker + Jib** (Dockerfile-less image build)

### Frontend
- ✅ React 18 + Vite
- ✅ Tailwind CSS (n11 esinlenmiş mor + pembe palet)
- ✅ React Router v6
- ✅ Context API (auth + cart state)
- ✅ Axios (interceptor ile JWT)
- ✅ **Saga Canlı Takip Modal'ı** (1 sn polling, gerçek zamanlı süreç görselleştirme)
- ✅ Responsive tasarım

### DevOps
- ✅ GitHub Actions CI (build + test pipeline)
- ✅ Environment-based secret management (.env)
- ✅ Docker compose ile tek komutla ayağa kalkma

## 🏗️ Mimari

```
                              ┌─────────────────────┐
                              │    API Gateway      │  :8763
                              │  (JWT validation,   │
                              │   CORS, routing)    │
                              └──────────┬──────────┘
                                         │
        ┌──────────────┬─────────────────┼─────────────────┬──────────────┐
        ▼              ▼                 ▼                 ▼              ▼
   ┌────────┐    ┌──────────┐      ┌──────────┐      ┌──────────┐   ┌──────────┐
   │  User  │    │ Product  │      │  Order   │      │  Stock   │   │ Payment  │
   │ :8766  │    │  :8767   │      │  :8769   │      │  :8768   │   │  :8770   │
   │  +DB   │    │   +DB    │      │   +DB    │      │   +DB    │   │   +DB    │
   └────────┘    └────▲─────┘      └────┬─────┘      └────┬─────┘   └─────▲────┘
                      │                 │                 │               │
                      │ Feign           │ Saga            │ Feign         │ RabbitMQ
                      │                 │ Orchestrator    │               │
                      └─────────────────┼─────────────────┘               │
                                        │                                 │
                                  ┌─────▼─────┐                           │
                                  │ RabbitMQ  │                           │
                                  │  :5672    │◄──────────────────────────┘
                                  └───────────┘

   Eureka :8761  ◄─── tüm servisler kayıtlı
   Config :8762  ◄─── centralized property server
   PostgreSQL :5433  ◄─── 5 ayrı database (userdb, productdb, stockdb, orderdb, paymentdb)
```

## 🔄 Saga Akışı

**Mutlu Yol** (CONFIRMED):
```
Order CREATED ──► Stock RESERVE (Feign) ──► RabbitMQ Payment Request
                                                      │
                                                      ▼
                                             Iyzico API Call
                                                      │
                                                      ▼
                                          RabbitMQ Payment Response (SUCCESS)
                                                      │
                                                      ▼
                                             Stock CONFIRM ──► Order CONFIRMED ✓
```

**Compensation** (Payment FAILED):
```
Order CREATED ──► Stock RESERVED ──► Payment Request ──► Iyzico DECLINED
                                                              │
                                                              ▼
                                                  RabbitMQ Payment Response (FAIL)
                                                              │
                                                              ▼
                                                  Stock CANCEL (compensation)
                                                              │
                                                              ▼
                                                  Order CANCELLED ✗
                                              (stok geri yüklendi)
```

**Stok Yetersiz** (FAILED_AT_STOCK):
```
Order CREATED ──► Stock RESERVE FAIL (insufficient stock)
                              │
                              ▼
                  Order CANCELLED (compensation gerekmedi,
                                   ödeme akışı hiç başlamadı)
```

## 🚀 Hızlı Başlangıç

### Gereksinimler
- Java 21
- Maven 3.9+
- Node.js 20+
- Docker Desktop
- PostgreSQL ve RabbitMQ Docker container'ları

### 1) Altyapı container'larını başlat
```bash
docker run -d --name ecommerce-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -p 5433:5432 \
  postgres:16

docker run -d --name ecommerce-rabbit \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management

# Database'leri oluştur
docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE userdb;"
docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE productdb;"
docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE stockdb;"
docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE orderdb;"
docker exec -it ecommerce-postgres psql -U postgres -c "CREATE DATABASE paymentdb;"
```

### 2) .env dosyasını oluştur
```bash
cp .env.example .env
# .env içine kendi Iyzico sandbox key'lerini yaz
```

### 3) Backend'i başlat
```bash
mvn clean install -DskipTests

# Sırasıyla başlat:
# discovery-server → config-server → api-gateway →
# user-service → product-service → stock-service →
# order-service → payment-service
```

IntelliJ kullananlar için: **Compound Run Configuration** ile tek tıkla 8 servis başlatabilirsin.

### 4) Frontend'i başlat
```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173
```

### 5) Test hesapları (otomatik seed)
- **Müşteri**: `ahmet` / `password123`
- **Yönetici**: `admin` / `admin123`

## 🔌 Erişim Noktaları

| Servis | URL | Açıklama |
|---|---|---|
| Frontend | http://localhost:5173 | Ana uygulama |
| API Gateway | http://localhost:8763 | Tüm API çağrıları |
| Eureka Dashboard | http://localhost:8761 | Service discovery panel |
| RabbitMQ Management | http://localhost:15672 | guest / guest |
| User Swagger | http://localhost:8766/swagger-ui.html | Auth API docs |
| Product Swagger | http://localhost:8767/swagger-ui.html | Product API docs |
| Stock Swagger | http://localhost:8768/swagger-ui.html | Stock API docs |
| Order Swagger | http://localhost:8769/swagger-ui.html | Order API docs |
| Payment Swagger | http://localhost:8770/swagger-ui.html | Payment API docs |

## 📝 Test Kartları (Iyzico Sandbox)

| Sonuç | Kart Numarası | Son Kul. | CVC |
|---|---|---|---|
| ✅ Onaylanan | `5528790000000008` | 12/2030 | 123 |
| ❌ Reddedilen | `4111111111111129` | 12/2030 | 123 |

Frontend'de Checkout sayfasında **tek tıkla otomatik dolduran butonlar** mevcut.

## 🧪 Testleri Çalıştır

```bash
mvn clean test
# JaCoCo HTML raporu: <service>/target/site/jacoco/index.html
```

Saga compensation logic için ~18 unit test mevcut (Mockito ile, Spring context yüklenmiyor — saniyeler içinde tamamlanır).

## 📦 Docker Image Build (Jib)

```bash
mvn clean install -DskipTests
mvn jib:dockerBuild
# → ecommerce-saga/<service>:0.0.1-SNAPSHOT image'ları local Docker'a kurulur
```

## 🔐 Güvenlik Notu

- Kart bilgileri **hiçbir zaman** veritabanına yazılmaz
- Sadece **maskelenmiş kart numarası** (`****0008`) saklanır
- Iyzico API key'leri `.env` dosyasında, git'e commit edilmez
- JWT secret'ı environment variable'dan okunur
