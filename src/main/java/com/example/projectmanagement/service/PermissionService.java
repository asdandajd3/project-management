package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Kiểm tra quyền truy cập của user
     * @param user User đang đăng nhập
     * @param permissionType Loại quyền cần kiểm tra
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasPermission(User user, String permissionType) {
        if (user == null) {
            return false;
        }
        
        // Nếu là ADMIN thì có tất cả quyền
        if ("ADMIN".equals(user.getVaiTro())) {
            return true;
        }
        
        // Nếu là USER thì kiểm tra theo staff
        if ("USER".equals(user.getVaiTro())) {
            System.out.println("=== DEBUG PERMISSION SERVICE ===");
            System.out.println("User: " + user.getTenDangNhap());
            System.out.println("User Role: " + user.getVaiTro());
            System.out.println("User Email: " + user.getTenDangNhap());
            
            // Tìm staff theo email (tenDangNhap)
            Staff staff = staffService.findByEmail(user.getTenDangNhap());
            
            if (staff != null) {
                System.out.println("Staff found: " + staff.getFullName());
                System.out.println("Staff adminId: " + staff.getAdminId());
                System.out.println("Permission requested: " + permissionType);
                
                boolean result = false;
                switch (permissionType) {
                    case "canViewProject":
                        result = staff.isCanViewProject();
                        break;
                    case "canEditProject":
                        result = staff.isCanEditProject();
                        break;
                    case "canViewContract":
                        result = staff.isCanViewContract();
                        break;
                    case "canEditContract":
                        result = staff.isCanEditContract();
                        break;
                    case "canViewStaff":
                        result = staff.isCanViewStaff();
                        break;
                    case "canEditStaff":
                        result = staff.isCanEditStaff();
                        break;
                    default:
                        result = false;
                }
                System.out.println("Permission result: " + result);
                System.out.println("=== END DEBUG ===");
                return result;
            } else {
                System.out.println("Staff not found for email: " + user.getTenDangNhap());
                System.out.println("=== END DEBUG ===");
            }
        }
        
        return false;
    }
    
    /**
     * Lấy thông tin staff của user
     * @param user User đang đăng nhập
     * @return Staff object hoặc null nếu không tìm thấy
     */
    public Staff getStaffForUser(User user) {
        if (user == null || !"USER".equals(user.getVaiTro())) {
            return null;
        }
        
        // Tìm staff theo email (tenDangNhap)
        return staffService.findByEmail(user.getTenDangNhap());
    }
} 