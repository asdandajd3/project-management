package com.example.projectmanagement.repository;

import com.example.projectmanagement.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByStatus(String status);
    List<Contract> findByAdminId(Long adminId);
    List<Contract> findByStatusContainingIgnoreCase(String keyword);
    List<Contract> findBySignedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Custom query để lọc theo status và adminId
    @Query("SELECT c FROM Contract c WHERE c.status = :status AND c.adminId = :adminId")
    List<Contract> findByStatusAndAdminId(@Param("status") String status, @Param("adminId") Long adminId);
    
    // Custom query để tìm kiếm theo keyword và adminId
    @Query("SELECT c FROM Contract c WHERE (c.contractName LIKE %:keyword% OR c.companyName LIKE %:keyword%) AND c.adminId = :adminId")
    List<Contract> searchByKeywordAndAdminId(@Param("keyword") String keyword, @Param("adminId") Long adminId);
} 