# Project Management với Docker

## Cài đặt và chạy ứng dụng

### Yêu cầu
- Docker
- Docker Compose

### Cách chạy

1. **Clone repository và di chuyển vào thư mục dự án:**
```bash
cd project-management
```

2. **Chạy ứng dụng với Docker Compose:**
```bash
docker-compose up -d
```

3. **Truy cập ứng dụng:**
- Ứng dụng: http://localhost:8082
- Database: localhost:5433

### Các lệnh hữu ích

**Xem logs:**
```bash
docker-compose logs -f app
```

**Dừng ứng dụng:**
```bash
docker-compose down
```

**Dừng và xóa dữ liệu:**
```bash
docker-compose down -v
```

**Rebuild ứng dụng:**
```bash
docker-compose up --build
```

### Cấu trúc Docker

- **PostgreSQL Database**: Chạy trên port 5433 (host) -> 5432 (container)
- **Spring Boot App**: Chạy trên port 8082
- **Volume**: Dữ liệu database được lưu trong volume `postgres_data`
- **Network**: Các container kết nối qua network `project-network`

### Environment Variables

Có thể tùy chỉnh thông qua file `docker-compose.yml`:
- `POSTGRES_DB`: Tên database
- `POSTGRES_USER`: Username database
- `POSTGRES_PASSWORD`: Password database
- `SPRING_DATASOURCE_URL`: URL kết nối database 