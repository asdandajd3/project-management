package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.StaffRepository;
import com.example.projectmanagement.service.StaffService;
import com.example.projectmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/home/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;
    
    public StaffController() {
        System.out.println("=== DEBUG STAFF CONTROLLER CONSTRUCTOR CALLED ===");
        System.out.println("=== DEBUG STAFF CONTROLLER CONSTRUCTOR CALLED ===");
        System.out.println("=== DEBUG STAFF CONTROLLER CONSTRUCTOR CALLED ===");
    }

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listStaff(Model model, HttpSession session) {
        try {
            System.out.println("=== DEBUG LIST STAFF METHOD CALLED ===");
            System.out.println("=== DEBUG LIST STAFF METHOD CALLED ===");
            System.out.println("=== DEBUG LIST STAFF METHOD CALLED ===");
            // Luôn lấy user từ Spring Security để đảm bảo chính xác
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedInUser = null;
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                // Lưu vào session để lần sau dùng
                if (loggedInUser != null) {
                    session.setAttribute("loggedInUser", loggedInUser);
                }
            }
        
        List<Staff> staffList;
        
        System.out.println("=== DEBUG STAFF LIST ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Logged in user: " + (loggedInUser != null ? loggedInUser.getTenDangNhap() : "null"));
        System.out.println("User ID: " + (loggedInUser != null ? loggedInUser.getId() : "null"));
        System.out.println("User Role: " + (loggedInUser != null ? loggedInUser.getVaiTro() : "null"));
        
        if (loggedInUser != null) {
            // Tất cả user (ADMIN và USER) chỉ xem được nhân viên có cùng adminId
            staffList = staffService.findByAdminId(loggedInUser.getId());
            System.out.println("Found " + staffList.size() + " staff for admin ID: " + loggedInUser.getId());
            staffList.forEach(s -> System.out.println("Staff: " + s.getFullName() + " (admin_id: " + s.getAdminId() + ")"));
        } else {
            // Nếu chưa đăng nhập thì redirect về login
            System.out.println("No user logged in, redirecting to login");
            return "redirect:/login";
        }
        System.out.println("=== END DEBUG ===");

        long activeCount = staffList.stream()
                .filter(s -> "Hoạt động".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long leaveCount = staffList.stream()
                .filter(s -> "Nghỉ phép".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long internCount = staffList.stream()
                .filter(s -> "Thực tập".equalsIgnoreCase(s.getStatus()))
                .count();

        model.addAttribute("staffList", staffList);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("leaveCount", leaveCount);
        model.addAttribute("internCount", internCount);
        model.addAttribute("loggedInUser", loggedInUser);
        return "staff/stafflist";
        } catch (Exception e) {
            System.out.println("=== DEBUG STAFF CONTROLLER ERROR ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== END DEBUG ===");
            throw e;
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        System.out.println("=== DEBUG STAFF ADD FORM METHOD CALLED ===");
        model.addAttribute("staff", new Staff());
        return "staff/staffform";
    }

    @PostMapping("/save")
    public String saveStaff(@ModelAttribute Staff staff,
                            @RequestParam("avatarFile") MultipartFile avatarFile,
                            HttpSession session) {
        System.out.println("=== DEBUG SAVE STAFF METHOD CALLED ===");
        System.out.println("=== DEBUG SAVE STAFF METHOD CALLED ===");
        System.out.println("=== DEBUG SAVE STAFF METHOD CALLED ===");
        System.out.println("=== DEBUG SAVE STAFF METHOD CALLED ===");
        System.out.println("=== DEBUG SAVE STAFF METHOD CALLED ===");
        try {
            // Set adminId cho tất cả user (ADMIN và USER)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedInUser = null;
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
            }
            if (loggedInUser != null) {
                staff.setAdminId(loggedInUser.getId());
                System.out.println("=== DEBUG SAVE STAFF ===");
                System.out.println("User ID: " + loggedInUser.getId());
                System.out.println("Staff adminId set to: " + staff.getAdminId());
                System.out.println("=== END DEBUG ===");
            }
            
            // Set thời gian tạo/cập nhật
            if (staff.getId() == null) {
                staff.setCreatedAt(LocalDateTime.now());
            }
            staff.setUpdatedAt(LocalDateTime.now());
            
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String filename = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + "/uploads/avatar";
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath); // Tạo thư mục nếu chưa có

                Path filePath = uploadPath.resolve(filename);
                avatarFile.transferTo(filePath.toFile());

                staff.setAvatar(filename);
            } else {
                if (staff.getId() != null) {
                    Staff existing = staffService.findById(staff.getId());
                    if (existing != null) {
                        staff.setAvatar(existing.getAvatar());
                    }
                }
            }

            staffService.save(staff);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/home/staff";
    }

    @PostMapping("/update")
    public String updateStaff(@ModelAttribute Staff staff, HttpSession session) {
        // Set adminId cho tất cả user (ADMIN và USER)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        if (loggedInUser != null) {
            staff.setAdminId(loggedInUser.getId());
            System.out.println("=== DEBUG UPDATE STAFF ===");
            System.out.println("User ID: " + loggedInUser.getId());
            System.out.println("Staff adminId set to: " + staff.getAdminId());
            System.out.println("=== END DEBUG ===");
        }
        
        staff.setUpdatedAt(LocalDateTime.now());
        staffService.save(staff);
        return "redirect:/home/staff";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println("=== DEBUG STAFF EDIT FORM ===");
        Staff staff = staffService.findById(id);
        model.addAttribute("staff", staff);
        return "staff/staffform";
    }

    @GetMapping("/detail/{id}")
    public String viewStaffDetail(@PathVariable Long id, Model model) {
        System.out.println("=== DEBUG STAFF DETAIL METHOD CALLED ===");
        System.out.println("Staff ID requested: " + id);
        try {
            Staff staff = staffService.findById(id);
            System.out.println("Staff found: " + (staff != null ? staff.getFullName() : "null"));
            if (staff != null) {
                System.out.println("Staff adminId: " + staff.getAdminId());
                System.out.println("Staff status: " + staff.getStatus());
                System.out.println("Staff canViewProject: " + staff.isCanViewProject());
            }
            model.addAttribute("staff", staff);
            System.out.println("=== END DEBUG STAFF DETAIL ===");
            return "staff/detail";
        } catch (Exception e) {
            System.out.println("=== DEBUG STAFF DETAIL ERROR ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== END DEBUG STAFF DETAIL ERROR ===");
            throw e;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        staffService.deleteById(id);
        return "redirect:/home/staff";
    }

    @GetMapping("/filter")
    public String filterByPosition(@RequestParam("position") String position, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        List<Staff> filteredList;
        
        if (loggedInUser != null) {
            // Tất cả user (ADMIN và USER) chỉ xem được nhân viên có cùng adminId
            List<Staff> userStaffList = staffService.findByAdminId(loggedInUser.getId());
            filteredList = userStaffList.stream()
                    .filter(s -> s.getPosition() != null && 
                               (position.equals(s.getPosition()) || 
                                s.getPosition().contains(position)))
                    .toList();
        } else {
            // Nếu chưa đăng nhập thì redirect về login
            return "redirect:/login";
        }
        
        long activeCount = filteredList.stream()
                .filter(s -> "Hoạt động".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long leaveCount = filteredList.stream()
                .filter(s -> "Nghỉ phép".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long internCount = filteredList.stream()
                .filter(s -> "Thực tập".equalsIgnoreCase(s.getStatus()))
                .count();

        model.addAttribute("staffList", filteredList);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("leaveCount", leaveCount);
        model.addAttribute("internCount", internCount);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("position", position);
        return "staff/stafflist";
    }
    @GetMapping("/search")
    public String searchStaff(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        List<Staff> staffList;
        
        if (loggedInUser != null) {
            // Tất cả user (ADMIN và USER) chỉ tìm kiếm trong nhân viên có cùng adminId
            List<Staff> userStaffList = staffService.findByAdminId(loggedInUser.getId());
            staffList = userStaffList.stream()
                    .filter(s -> s.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        } else {
            // Nếu chưa đăng nhập thì redirect về login
            return "redirect:/login";
        }
        
        long activeCount = staffList.stream()
                .filter(s -> "Hoạt động".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long leaveCount = staffList.stream()
                .filter(s -> "Nghỉ phép".equalsIgnoreCase(s.getStatus()))
                .count();
        
        long internCount = staffList.stream()
                .filter(s -> "Thực tập".equalsIgnoreCase(s.getStatus()))
                .count();

        model.addAttribute("staffList", staffList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("leaveCount", leaveCount);
        model.addAttribute("internCount", internCount);
        model.addAttribute("loggedInUser", loggedInUser);
        return "staff/stafflist";
    }
}
