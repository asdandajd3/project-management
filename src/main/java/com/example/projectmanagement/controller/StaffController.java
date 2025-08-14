package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.StaffRepository;
import com.example.projectmanagement.service.StaffService;
import com.example.projectmanagement.service.UserService;
import com.example.projectmanagement.service.PermissionService;
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
    
    @Autowired
    private PermissionService permissionService;

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
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin thấy nhân sự của mình
                staffList = staffService.findByAdminId(loggedInUser.getId());
                System.out.println("Found " + staffList.size() + " staff for admin ID: " + loggedInUser.getId());
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ thấy nhân sự nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewStaff")) {
                    // Lấy adminId từ maAdmin của user thay vì từ staff
                    String maAdmin = loggedInUser.getMaAdmin();
                    if (maAdmin != null && !maAdmin.trim().isEmpty()) {
                        try {
                            Long adminId = Long.parseLong(maAdmin);
                            staffList = staffService.findByAdminId(adminId);
                            System.out.println("Found " + staffList.size() + " staff for admin ID: " + adminId);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid maAdmin format: " + maAdmin);
                            staffList = List.of();
                        }
                    } else {
                        // Fallback: lấy từ staff nếu maAdmin không có
                        Staff staff = permissionService.getStaffForUser(loggedInUser);
                        if (staff != null) {
                            staffList = staffService.findByAdminId(staff.getAdminId());
                            System.out.println("Found " + staffList.size() + " staff for staff admin ID: " + staff.getAdminId());
                        } else {
                            staffList = List.of();
                        }
                    }
                } else {
                    staffList = List.of();
                }
            } else {
                staffList = List.of();
            }
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
        
        // Thêm thông tin quyền để hiển thị/ẩn nút
        boolean canEditStaff = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditStaff");
        model.addAttribute("canEditStaff", canEditStaff);
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
        
        // Kiểm tra quyền chỉnh sửa nhân sự
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditStaff")) {
            return "redirect:/error/access-denied?resource=staff";
        }
        
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
            
            // JPA Auditing sẽ tự động set thời gian tạo/cập nhật
            
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

            // Lưu staff trước để có ID
            staffService.save(staff);
            
            // Tạo bản ghi User cho nhân sự mới
            if (staff.getId() != null) {
                // Kiểm tra xem email đã tồn tại chưa
                if (!userService.existsByTenDangNhap(staff.getEmail())) {
                    User newUser = new User();
                    newUser.setTenDangNhap(staff.getEmail()); // Email làm tên đăng nhập
                    newUser.setMatKhau("123"); // Mật khẩu mặc định
                    newUser.setVaiTro("USER"); // Vai trò mặc định là USER
                    newUser.setMaAdmin(loggedInUser.getId().toString()); // Mã admin
                    
                    // Sử dụng registerUser để tự động mã hóa mật khẩu và tạo mã nhân viên
                    boolean userCreated = userService.registerUser(newUser);
                    
                    if (userCreated) {
                        System.out.println("=== DEBUG USER CREATED SUCCESSFULLY ===");
                        System.out.println("Created user for staff ID: " + staff.getId());
                        System.out.println("User email: " + staff.getEmail());
                        System.out.println("User maAdmin: " + newUser.getMaAdmin());
                        System.out.println("=== END DEBUG ===");
                    } else {
                        System.out.println("=== DEBUG USER CREATION FAILED ===");
                        System.out.println("Failed to create user for staff ID: " + staff.getId());
                        System.out.println("=== END DEBUG ===");
                    }
                } else {
                    System.out.println("=== DEBUG USER ALREADY EXISTS ===");
                    System.out.println("User with email " + staff.getEmail() + " already exists");
                    System.out.println("=== END DEBUG ===");
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/home/staff";
    }

    @PostMapping("/update")
    public String updateStaff(@ModelAttribute Staff staff, HttpSession session) {
        // Luôn lấy thông tin staff hiện tại từ database để giữ nguyên adminId
        Staff existingStaff = staffService.findById(staff.getId());
        if (existingStaff != null) {
            // Giữ nguyên adminId từ database
            staff.setAdminId(existingStaff.getAdminId());
            System.out.println("=== DEBUG UPDATE STAFF - KEEPING EXISTING ADMIN ID ===");
            System.out.println("Staff ID: " + staff.getId());
            System.out.println("Staff adminId from database: " + existingStaff.getAdminId());
            System.out.println("Staff adminId set to: " + staff.getAdminId());
            System.out.println("=== END DEBUG ===");
        } else {
            System.out.println("=== DEBUG UPDATE STAFF - STAFF NOT FOUND ===");
            System.out.println("Staff ID: " + staff.getId());
            System.out.println("=== END DEBUG ===");
            return "redirect:/home/staff";
        }
        
        // JPA Auditing sẽ tự động set thời gian cập nhật
        staffService.updateStaff(staff);
        return "redirect:/home/staff";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println("=== DEBUG STAFF EDIT FORM ===");
        
        // Kiểm tra quyền chỉnh sửa nhân sự
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditStaff")) {
            return "redirect:/error/access-denied?resource=staff";
        }
        
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
        // Kiểm tra quyền chỉnh sửa nhân sự (quyền xóa = quyền chỉnh sửa)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditStaff")) {
            return "redirect:/error/access-denied?resource=staff";
        }
        
        staffService.deleteById(id);
        return "redirect:/home/staff";
    }

    @GetMapping("/filter")
    public String filterByPosition(@RequestParam("position") String position, Model model, HttpSession session) {
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        List<Staff> filteredList = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin lọc nhân sự của mình
                List<Staff> userStaffList = staffService.findByAdminId(loggedInUser.getId());
                filteredList = userStaffList.stream()
                        .filter(s -> s.getPosition() != null && 
                                   (position.equals(s.getPosition()) || 
                                    s.getPosition().contains(position)))
                        .toList();
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ lọc nhân sự nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewStaff")) {
                    Staff staff = permissionService.getStaffForUser(loggedInUser);
                    if (staff != null) {
                        List<Staff> userStaffList = staffService.findByAdminId(staff.getAdminId());
                        filteredList = userStaffList.stream()
                                .filter(s -> s.getPosition() != null && 
                                           (position.equals(s.getPosition()) || 
                                            s.getPosition().contains(position)))
                                .toList();
                    }
                }
            }
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
        
        // Thêm thông tin quyền để hiển thị/ẩn nút
        boolean canEditStaff = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditStaff");
        model.addAttribute("canEditStaff", canEditStaff);
        return "staff/stafflist";
    }
    @GetMapping("/search")
    public String searchStaff(@RequestParam("keyword") String keyword, Model model, HttpSession session) {
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        List<Staff> staffList = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin tìm kiếm trong nhân sự của mình
                List<Staff> userStaffList = staffService.findByAdminId(loggedInUser.getId());
                staffList = userStaffList.stream()
                        .filter(s -> s.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                        .toList();
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ tìm kiếm nhân sự nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewStaff")) {
                    Staff staff = permissionService.getStaffForUser(loggedInUser);
                    if (staff != null) {
                        List<Staff> userStaffList = staffService.findByAdminId(staff.getAdminId());
                        staffList = userStaffList.stream()
                                .filter(s -> s.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                                .toList();
                    }
                }
            }
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
        
        // Thêm thông tin quyền để hiển thị/ẩn nút
        boolean canEditStaff = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditStaff");
        model.addAttribute("canEditStaff", canEditStaff);
        return "staff/stafflist";
    }
}
