package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user,
                        HttpSession session,
                        Model model) {
        User loggedIn = userService.login(user.getTenDangNhap(), user.getMatKhau());
        if (loggedIn != null) {
            session.setAttribute("loggedInUser", loggedIn);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

