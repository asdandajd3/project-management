package com.example.projectmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tenDangNhap;

    @Column(nullable = false)
    private String matKhau;

    @Column(nullable = false)
    private String vaiTro = "USER";

    @Column(unique = true)
    private String maNhanVien;

    @Column(unique = true)
    private String maAdmin;

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getMaAdmin() { return maAdmin; }
    public void setMaAdmin(String maAdmin) { this.maAdmin = maAdmin; }

    // Helper methods để tương thích với code cũ
    public String getEmail() { return tenDangNhap; }
    public void setEmail(String email) { this.tenDangNhap = email; }

    public String getPassword() { return matKhau; }
    public void setPassword(String password) { this.matKhau = password; }

    public String getRole() { return vaiTro; }
    public void setRole(String role) { this.vaiTro = role; }

    public String getFullName() { return tenDangNhap; }
    public void setFullName(String fullName) { this.tenDangNhap = fullName; }
}

