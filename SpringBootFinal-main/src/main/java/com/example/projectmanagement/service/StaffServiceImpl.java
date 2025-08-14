package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        // Tự động set adminId nếu chưa có
        if (staff.getAdminId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                User loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                if (loggedInUser != null) {
                    staff.setAdminId(loggedInUser.getId());
                    System.out.println("=== DEBUG STAFF SERVICE ===");
                    System.out.println("User ID: " + loggedInUser.getId());
                    System.out.println("Staff adminId set to: " + staff.getAdminId());
                    System.out.println("=== END DEBUG ===");
                }
            }
        }
        staffRepository.save(staff);
    }

    @Override
    public Staff findById(Long id) {
        return staffRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }
}
