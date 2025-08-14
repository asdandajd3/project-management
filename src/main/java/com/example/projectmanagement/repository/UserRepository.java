package com.example.projectmanagement.repository;

import com.example.projectmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTenDangNhap(String tenDangNhap);
    boolean existsByTenDangNhap(String tenDangNhap);
}
