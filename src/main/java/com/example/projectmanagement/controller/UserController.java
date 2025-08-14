package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Hiển thị danh sách users
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    // Hiển thị form thêm user mới
    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    // Hiển thị form sửa user
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id).orElse(new User());
        model.addAttribute("user", user);
        return "users/form";
    }

    // Lưu user (thêm mới hoặc cập nhật)
    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, 
                          BindingResult result, 
                          Model model) {
        if (result.hasErrors()) {
            return "users/form";
        }

        // Kiểm tra tên đăng nhập trùng lặp khi thêm mới
        if (user.getId() == null && userService.existsByTenDangNhap(user.getTenDangNhap())) {
            result.rejectValue("tenDangNhap", "error.user", "Tên đăng nhập đã tồn tại");
            return "users/form";
        }

        // Kiểm tra tên đăng nhập trùng lặp khi cập nhật
        if (user.getId() != null) {
            User existingUser = userService.getUserById(user.getId()).orElse(null);
            if (existingUser != null && !existingUser.getTenDangNhap().equals(user.getTenDangNhap()) 
                && userService.existsByTenDangNhap(user.getTenDangNhap())) {
                result.rejectValue("tenDangNhap", "error.user", "Tên đăng nhập đã tồn tại");
                return "users/form";
            }
        }

        if (userService.registerUser(user)) {
            return "redirect:/users";
        } else {
            model.addAttribute("error", "Có lỗi xảy ra khi lưu người dùng");
            return "users/form";
        }
    }

    // Xóa user
    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
} 