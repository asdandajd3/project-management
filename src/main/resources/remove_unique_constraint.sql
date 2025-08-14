-- Xóa unique constraint trên ma_admin trong bảng app_user
-- Vì mỗi admin có thể có nhiều staff

-- Tìm tên constraint
SELECT conname FROM pg_constraint WHERE conrelid = 'app_user'::regclass AND contype = 'u';

-- Xóa unique constraint (thay thế tên constraint thực tế)
-- ALTER TABLE app_user DROP CONSTRAINT IF EXISTS uk_t1v60khnsj28qrt48nhcpd1i7;

-- Hoặc tạo lại bảng app_user không có unique constraint trên ma_admin
DROP TABLE IF EXISTS app_user CASCADE;

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    ten_dang_nhap VARCHAR(255) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro VARCHAR(50) NOT NULL DEFAULT 'USER',
    ma_nhan_vien VARCHAR(255) UNIQUE,
    ma_admin VARCHAR(255) -- Bỏ unique constraint
);

-- Thêm lại dữ liệu mẫu
INSERT INTO app_user (ten_dang_nhap, mat_khau, vai_tro, ma_admin, ma_nhan_vien) VALUES 
('admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', 'ADMIN20250728150700', NULL),
('user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', NULL, 'NV20250728150701')
ON CONFLICT (ten_dang_nhap) DO NOTHING; 