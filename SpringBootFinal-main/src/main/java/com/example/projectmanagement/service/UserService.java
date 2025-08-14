package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(User user) {
        // Nếu là user mới (id == null)
        if (user.getId() == null) {
            if (userRepository.existsByTenDangNhap(user.getTenDangNhap())) {
                return false;
            }
            
            // Mã hóa mật khẩu
            user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
            
            // Tự động tạo mã nhân viên hoặc mã admin dựa vào vai trò
            if ("ADMIN".equals(user.getVaiTro())) {
                user.setMaAdmin(generateMaAdmin());
                user.setMaNhanVien(null);
            } else {
                user.setMaNhanVien(generateMaNhanVien());
                user.setMaAdmin(null);
            }
            
            userRepository.save(user);
            return true;
        } else {
            // Nếu là cập nhật user
            return updateUser(user);
        }
    }

    // Tạo mã admin tự động
    private String generateMaAdmin() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ADMIN" + timestamp;
    }

    // Tạo mã nhân viên tự động
    private String generateMaNhanVien() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "NV" + timestamp;
    }
    public User login(String tenDangNhap, String rawPassword) {
        return userRepository.findByTenDangNhap(tenDangNhap)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getMatKhau()))
                .orElse(null);
    }

    // Lấy tất cả users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Cập nhật user
    public boolean updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            // Nếu password không được cập nhật, giữ nguyên password cũ
            if (user.getMatKhau() == null || user.getMatKhau().isEmpty()) {
                User existingUser = userRepository.findById(user.getId()).orElse(null);
                if (existingUser != null) {
                    user.setMatKhau(existingUser.getMatKhau());
                }
            } else {
                // Mã hóa password mới
                user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Xóa user
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Kiểm tra tên đăng nhập tồn tại
    public boolean existsByTenDangNhap(String tenDangNhap) {
        return userRepository.existsByTenDangNhap(tenDangNhap);
    }

    // Lấy user theo tên đăng nhập
    public User getUserByTenDangNhap(String tenDangNhap) {
        return userRepository.findByTenDangNhap(tenDangNhap).orElse(null);
    }


    // Load User for login
    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        User user = userRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        return new org.springframework.security.core.userdetails.User(
                user.getTenDangNhap(),
                user.getMatKhau(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getVaiTro()))
        );
    }
}
