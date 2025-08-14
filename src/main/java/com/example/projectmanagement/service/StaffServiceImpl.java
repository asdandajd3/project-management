package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserService userService;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository, UserService userService) {
        this.staffRepository = staffRepository;
        this.userService = userService;
    }

    @Override
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    @Override
    public List<Staff> findByAdminId(Long adminId) {
        return staffRepository.findByAdminId(adminId);
    }

    @Override
    public long countByStatus(String status) {
        return staffRepository.countByStatus(status);
    }

    @Override
    public void save(Staff staff) {
        // Tự động set adminId chỉ khi tạo mới staff (staff.getId() == null)
        User loggedInUser = null;
        if (staff.getId() == null && staff.getAdminId() == null) {
            // Chỉ set adminId khi tạo mới và chưa có adminId
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                if (loggedInUser != null) {
                    staff.setAdminId(loggedInUser.getId());
                    System.out.println("=== DEBUG STAFF SERVICE - SETTING ADMIN ID FOR NEW STAFF ===");
                    System.out.println("User ID: " + loggedInUser.getId());
                    System.out.println("Staff adminId set to: " + staff.getAdminId());
                    System.out.println("=== END DEBUG ===");
                }
            }
        } else if (staff.getAdminId() != null) {
            // Nếu đã có adminId, lấy thông tin admin
            loggedInUser = userService.getUserById(staff.getAdminId()).orElse(null);
        }
        
        // Lưu staff trước để có ID
        System.out.println("=== BEFORE SAVE STAFF ===");
        System.out.println("Staff ID before save: " + staff.getId());
        System.out.println("Staff Email: " + staff.getEmail());
        System.out.println("Staff adminId: " + staff.getAdminId());
        
        staffRepository.save(staff);
        
        System.out.println("=== AFTER SAVE STAFF ===");
        System.out.println("Staff ID after save: " + staff.getId());
        System.out.println("Staff Email: " + staff.getEmail());
        System.out.println("Staff adminId: " + staff.getAdminId());
        
        // Tự động tạo tài khoản User cho staff mới (chỉ khi tạo mới, không phải chỉnh sửa)
        System.out.println("=== DEBUG AUTO CREATE USER ACCOUNT ===");
        System.out.println("loggedInUser: " + (loggedInUser != null ? loggedInUser.getTenDangNhap() : "null"));
        System.out.println("staff.getEmail(): " + staff.getEmail());
        System.out.println("staff.getEmail() != null: " + (staff.getEmail() != null));
        System.out.println("staff.getEmail().trim().isEmpty(): " + (staff.getEmail() != null ? staff.getEmail().trim().isEmpty() : "N/A"));
        System.out.println("staff.getId() before save: " + staff.getId());
        
        // Chỉ tạo tài khoản USER khi tạo mới staff (staff.getId() == null)
        if (loggedInUser != null && staff.getEmail() != null && !staff.getEmail().trim().isEmpty() && staff.getId() == null) {
            System.out.println("Creating new staff, proceeding to create account...");
            try {
                // Kiểm tra xem đã có tài khoản với email này chưa
                String emailToCheck = staff.getEmail().trim().toLowerCase();
                System.out.println("Checking for existing user with email: " + emailToCheck);
                
                User existingUser = userService.getUserByTenDangNhap(emailToCheck);
                System.out.println("existingUser: " + (existingUser != null ? existingUser.getTenDangNhap() : "null"));
                
                if (existingUser == null) {
                    // Tạo tài khoản mới
                    User newUser = new User();
                    newUser.setTenDangNhap(emailToCheck); // Email làm tên đăng nhập (đã chuẩn hóa)
                    newUser.setMatKhau("123"); // Mật khẩu mặc định
                    newUser.setVaiTro("USER"); // Role mặc định
                    newUser.setMaNhanVien(staff.getId().toString()); // ID của staff
                    newUser.setMaAdmin(staff.getAdminId().toString()); // ID của admin mà staff thuộc về
                    
                    userService.save(newUser);
                    
                    System.out.println("=== AUTO CREATE USER ACCOUNT SUCCESS ===");
                    System.out.println("Staff ID: " + staff.getId());
                    System.out.println("Staff Email: " + staff.getEmail());
                    System.out.println("Created User Account: " + newUser.getTenDangNhap());
                    System.out.println("Default Password: 123");
                    System.out.println("Role: " + newUser.getVaiTro());
                    System.out.println("MaNhanVien: " + newUser.getMaNhanVien());
                    System.out.println("MaAdmin: " + newUser.getMaAdmin());
                    System.out.println("=== END AUTO CREATE SUCCESS ===");
                } else {
                    System.out.println("=== USER ACCOUNT ALREADY EXISTS ===");
                    System.out.println("Email: " + staff.getEmail() + " already has an account");
                    System.out.println("Existing user ID: " + existingUser.getId());
                    System.out.println("=== END EXISTS CHECK ===");
                }
            } catch (Exception e) {
                System.err.println("=== ERROR CREATING USER ACCOUNT ===");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("=== END ERROR ===");
            }
        } else {
            System.out.println("=== CONDITIONS NOT MET ===");
            if (loggedInUser == null) {
                System.out.println("Reason: loggedInUser is null");
            }
            if (staff.getEmail() == null) {
                System.out.println("Reason: staff.getEmail() is null");
            }
            if (staff.getEmail() != null && staff.getEmail().trim().isEmpty()) {
                System.out.println("Reason: staff.getEmail() is empty");
            }
            if (staff.getId() != null) {
                System.out.println("Reason: staff.getId() is not null (this is an update, not create)");
            }
            System.out.println("=== END CONDITIONS NOT MET ===");
        }
        System.out.println("=== END DEBUG AUTO CREATE USER ACCOUNT ===");
    }
    
    /**
     * Update staff mà không thay đổi adminId
     */
    public void updateStaff(Staff staff) {
        System.out.println("=== DEBUG UPDATE STAFF SERVICE ===");
        System.out.println("Staff ID: " + staff.getId());
        System.out.println("Staff adminId: " + staff.getAdminId());
        System.out.println("=== END DEBUG ===");
        
        // Chỉ update, không thay đổi adminId
        staff.setUpdatedAt(LocalDateTime.now());
        staffRepository.save(staff);
    }

    @Override
    public Staff findById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    @Override
    public Staff findByEmail(String email) {
        return staffRepository.findByEmail(email);
    }

    @Override
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }
}
