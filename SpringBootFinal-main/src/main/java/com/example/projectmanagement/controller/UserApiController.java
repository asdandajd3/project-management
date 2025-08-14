package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    // Lấy danh sách tất cả users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo user mới
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.registerUser(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        if (userService.updateUser(user)) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Kiểm tra tên đăng nhập tồn tại
    @GetMapping("/check-username/{tenDangNhap}")
    public ResponseEntity<Boolean> checkTenDangNhapExists(@PathVariable String tenDangNhap) {
        return ResponseEntity.ok(userService.existsByTenDangNhap(tenDangNhap));
    }


} 