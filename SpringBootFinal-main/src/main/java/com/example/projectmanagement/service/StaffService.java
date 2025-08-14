package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Staff;
import java.util.List;

public interface StaffService {
    List<Staff> findAll();
    List<Staff> findByAdminId(Long adminId);
    long countByStatus(String status);
    void save(Staff staff);
    Staff findById(Long id);
    void deleteById(Long id);
}
