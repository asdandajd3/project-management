-- Khởi tạo dữ liệu mẫu cho bảng app_user
-- Mật khẩu được mã hóa bằng BCrypt (password: admin)

INSERT INTO app_user (ten_dang_nhap, mat_khau, vai_tro, ma_admin, ma_nhan_vien) VALUES 
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ADMIN20250728150700', NULL),
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NULL, 'NV20250728150701')
ON CONFLICT (ten_dang_nhap) DO NOTHING; 