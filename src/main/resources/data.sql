-- Khởi tạo dữ liệu mẫu cho bảng app_user
-- Mật khẩu được mã hóa bằng BCrypt (password: admin)

INSERT INTO app_user (ten_dang_nhap, mat_khau, vai_tro, ma_admin, ma_nhan_vien) VALUES 
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ADMIN20250728150700', NULL),
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NULL, 'NV20250728150701')
ON CONFLICT (ten_dang_nhap) DO NOTHING;

-- Thêm cột admin_id vào bảng project nếu chưa có
ALTER TABLE project ADD COLUMN IF NOT EXISTS admin_id BIGINT;

-- Thêm cột progress vào bảng project nếu chưa có
ALTER TABLE project ADD COLUMN IF NOT EXISTS progress INTEGER DEFAULT 0;

-- Cập nhật admin_id cho các dự án hiện có (gán cho admin đầu tiên)
UPDATE project SET admin_id = (SELECT id FROM app_user WHERE vai_tro = 'ADMIN' LIMIT 1) WHERE admin_id IS NULL;

-- Cập nhật progress cho các dự án hiện có
UPDATE project SET progress = 0 WHERE progress IS NULL;

-- Thêm dữ liệu mẫu cho bảng contract
INSERT INTO contract (contract_name, company_name, description, currency, value, term, status, signed_date, expriry_date, create_at, update_at, admin_id) VALUES 
('Hợp đồng phát triển website', 'Công ty ABC', 'Phát triển website thương mại điện tử', 'VNĐ', 500000000, 6, 'Đang thực hiện', '2024-01-15 09:00:00', '2024-07-15 09:00:00', '2024-01-15 09:00:00', '2024-01-15 09:00:00', (SELECT id FROM app_user WHERE vai_tro = 'ADMIN' LIMIT 1)),
('Hợp đồng bảo trì hệ thống', 'Công ty XYZ', 'Bảo trì và nâng cấp hệ thống quản lý', 'VNĐ', 300000000, 12, 'Chờ duyệt', '2024-02-01 10:00:00', '2025-02-01 10:00:00', '2024-02-01 10:00:00', '2024-02-01 10:00:00', (SELECT id FROM app_user WHERE vai_tro = 'ADMIN' LIMIT 1)),
('Hợp đồng tư vấn IT', 'Công ty DEF', 'Tư vấn chuyển đổi số', 'USD', 50000, 3, 'Hoàn thành', '2023-12-01 08:00:00', '2024-03-01 08:00:00', '2023-12-01 08:00:00', '2024-03-01 08:00:00', (SELECT id FROM app_user WHERE vai_tro = 'ADMIN' LIMIT 1))
ON CONFLICT DO NOTHING; 