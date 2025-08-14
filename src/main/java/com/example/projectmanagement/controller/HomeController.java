package com.example.projectmanagement.controller;

import com.example.projectmanagement.service.ProjectService;
import com.example.projectmanagement.service.ContractService;
import com.example.projectmanagement.service.StaffService;
import com.example.projectmanagement.service.PermissionService;
import com.example.projectmanagement.entity.Project;
import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // ✅ import đúng
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/home")
    public String home(Model model) {
        // Debug: Lấy user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        System.out.println("=== DEBUG HOME ===");
        System.out.println("User: " + (loggedInUser != null ? loggedInUser.getTenDangNhap() : "null"));
        System.out.println("User ID: " + (loggedInUser != null ? loggedInUser.getId() : "null"));
        System.out.println("User Role: " + (loggedInUser != null ? loggedInUser.getVaiTro() : "null"));
        System.out.println("=== END DEBUG HOME ===");
        
        // Thống kê theo quyền truy cập
        if (loggedInUser != null) {
            long runningCount = 0;
            long contractPending = 0;
            long staffCount = 0;
            
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin thấy tất cả dữ liệu của mình
                runningCount = projectService.findByAdminId(loggedInUser.getId()).stream()
                        .filter(p -> "Đang thực hiện".equals(p.getStatus()))
                        .count();
                contractPending = contractService.findByAdminId(loggedInUser.getId()).stream()
                        .filter(c -> "Chờ duyệt".equals(c.getStatus()))
                        .count();
                staffCount = staffService.findByAdminId(loggedInUser.getId()).size();
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ thấy dữ liệu theo quyền của staff
                Staff staff = permissionService.getStaffForUser(loggedInUser);
                if (staff != null) {
                    // Chỉ đếm nếu có quyền xem
                    if (permissionService.hasPermission(loggedInUser, "canViewProject")) {
                        runningCount = projectService.findByAdminId(staff.getAdminId()).stream()
                                .filter(p -> "Đang thực hiện".equals(p.getStatus()))
                                .count();
                    }
                    if (permissionService.hasPermission(loggedInUser, "canViewContract")) {
                        contractPending = contractService.findByAdminId(staff.getAdminId()).stream()
                                .filter(c -> "Chờ duyệt".equals(c.getStatus()))
                                .count();
                    }
                    if (permissionService.hasPermission(loggedInUser, "canViewStaff")) {
                        staffCount = staffService.findByAdminId(staff.getAdminId()).size();
                    }
                }
            }
            
            model.addAttribute("runningCount", runningCount);
            model.addAttribute("contractPending", contractPending);
            model.addAttribute("staffCount", staffCount);
        } else {
            model.addAttribute("runningCount", 0);
            model.addAttribute("contractPending", 0);
            model.addAttribute("staffCount", 0);
        }
        
        // Tính tổng doanh thu từ hợp đồng
        Double totalRevenue = 0.0;
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                totalRevenue = contractService.calculateTotalRevenueByAdminId(loggedInUser.getId());
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                Staff staff = permissionService.getStaffForUser(loggedInUser);
                if (staff != null && permissionService.hasPermission(loggedInUser, "canViewContract")) {
                    totalRevenue = contractService.calculateTotalRevenueByAdminId(staff.getAdminId());
                }
            }
        }
        
        // Format tổng doanh thu
        String formattedRevenue;
        if (totalRevenue != null && totalRevenue > 0) {
            if (totalRevenue >= 1_000_000_000) {
                formattedRevenue = String.format("%.1fB VND", totalRevenue / 1_000_000_000);
            } else if (totalRevenue >= 1_000_000) {
                formattedRevenue = String.format("%.1fM VND", totalRevenue / 1_000_000);
            } else if (totalRevenue >= 1_000) {
                formattedRevenue = String.format("%.1fK VND", totalRevenue / 1_000);
            } else {
                formattedRevenue = String.format("%.0f VND", totalRevenue);
            }
        } else {
            formattedRevenue = "0 VND";
        }
        
        model.addAttribute("totalRevenue", formattedRevenue);

        // Lấy dự án gần đây theo quyền truy cập
        List<Project> recentProjects = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                recentProjects = projectService.findRecentProjectsByAdminId(loggedInUser.getId(), 5);
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                Staff staff = permissionService.getStaffForUser(loggedInUser);
                if (staff != null && permissionService.hasPermission(loggedInUser, "canViewProject")) {
                    recentProjects = projectService.findRecentProjectsByAdminId(staff.getAdminId(), 5);
                }
            }
        }
        model.addAttribute("recentProjects", recentProjects);

        return "home/home";
    }
}
