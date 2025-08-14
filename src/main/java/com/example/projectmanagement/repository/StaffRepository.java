package com.example.projectmanagement.repository;


import com.example.projectmanagement.entity.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    long countByStatus(String status);
    List<Staff> findByPosition(String position);
    List<Staff> findByFullNameContainingIgnoreCase(String keyword);
    List<Staff> findByAdminId(Long adminId);
    Staff findByEmail(String email);
}
