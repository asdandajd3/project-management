# Hệ thống Quản lý Người dùng

## Tổng quan

Hệ thống quản lý người dùng với các chức năng:
- Đăng ký/Đăng nhập
- Quản lý thông tin người dùng (CRUD)
- Phân quyền theo vai trò
- Mã hóa mật khẩu bằng BCrypt
- Tự động tạo mã nhân viên và mã admin

## Cấu trúc Database

### Bảng `app_user`
```sql
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    ten_dang_nhap VARCHAR(255) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro VARCHAR(255) NOT NULL DEFAULT 'USER',
    ma_nhan_vien VARCHAR(255) UNIQUE,
    ma_admin VARCHAR(255) UNIQUE
);
```

## Tài khoản mẫu

| Tên đăng nhập | Mật khẩu | Vai trò | Mã admin | Mã nhân viên |
|---------------|----------|--------|----------|--------------|
| admin@example.com | 123456 | ADMIN | ADMIN20250728150700 | - |
| user@example.com | 123456 | USER | - | NV20250728150701 |

## Quy tắc tạo mã

### Mã Admin
- Format: `ADMIN` + timestamp (yyyyMMddHHmmss)
- Ví dụ: `ADMIN20250728150700`

### Mã Nhân viên
- Format: `NV` + timestamp (yyyyMMddHHmmss)
- Ví dụ: `NV20250728150701`

## API Endpoints

### REST API (cần authentication)

#### Lấy danh sách users
```http
GET /api/users
```

#### Lấy user theo ID
```http
GET /api/users/{id}
```

#### Tạo user mới
```http
POST /api/users
Content-Type: application/json

{
    "tenDangNhap": "user@example.com",
    "matKhau": "123456",
    "vaiTro": "USER"
}
```

#### Cập nhật user
```http
PUT /api/users/{id}
Content-Type: application/json

{
    "tenDangNhap": "user@example.com",
    "matKhau": "123456",
    "vaiTro": "MANAGER"
}
```

#### Xóa user
```http
DELETE /api/users/{id}
```

#### Kiểm tra tên đăng nhập tồn tại
```http
GET /api/users/check-username/{tenDangNhap}
```

### Web Endpoints

#### Trang đăng nhập
```
GET /login
```

#### Trang đăng ký
```
GET /register
```

#### Danh sách users
```
GET /users
```

#### Form thêm/sửa user
```
GET /users/new
GET /users/{id}/edit
```

## Cách sử dụng

### 1. Đăng nhập
- Truy cập: http://localhost:8082/login
- Sử dụng tài khoản mẫu ở trên

### 2. Quản lý users
- Truy cập: http://localhost:8082/users
- Thêm, sửa, xóa users
- Xem mã nhân viên và mã admin được tạo tự động

### 3. API Testing
```bash
# Lấy danh sách users (cần authentication)
curl -H "Authorization: Bearer <token>" http://localhost:8082/api/users

# Kiểm tra tên đăng nhập
curl http://localhost:8082/api/users/check-username/admin@example.com
```

## Tính năng đặc biệt

### Tự động tạo mã
- Khi tạo user với vai trò `ADMIN`: tự động tạo `ma_admin`, `ma_nhan_vien = null`
- Khi tạo user với vai trò `USER`: tự động tạo `ma_nhan_vien`, `ma_admin = null`

### Validation
- Tên đăng nhập phải unique
- Mật khẩu được mã hóa bằng BCrypt
- Vai trò mặc định là `USER`

### Security
- Session-based authentication
- Role-based authorization
- CSRF protection
- Password encryption

## Cấu hình Docker

### Chạy ứng dụng
```bash
cd project-management
docker compose up -d
```

### Truy cập
- Ứng dụng: http://localhost:8082
- Database: localhost:5433

### Lệnh hữu ích
```bash
# Xem logs
docker compose logs -f app

# Dừng ứng dụng
docker compose down

# Rebuild và chạy
docker compose up --build -d
``` 