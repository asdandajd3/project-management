package com.example.projectmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {
    
    @GetMapping("/error/access-denied")
    public String accessDenied(@RequestParam(required = false) String resource, Model model) {
        String message = "Bạn không có quyền truy cập";
        if (resource != null) {
            switch (resource) {
                case "project":
                    message = "Bạn không có quyền chỉnh sửa dự án";
                    break;
                case "contract":
                    message = "Bạn không có quyền chỉnh sửa hợp đồng";
                    break;
                case "staff":
                    message = "Bạn không có quyền chỉnh sửa nhân sự";
                    break;
                default:
                    message = "Bạn không có quyền truy cập " + resource;
            }
        }
        model.addAttribute("errorMessage", message);
        return "error/access-denied";
    }
} 