package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Contract;
import java.util.List;

public interface ContractService {
    List<Contract> findAll();
    Contract findById(Long id);
    void save(Contract contract);
    void deleteById(Long id);
    List<Contract> findByAdminId(Long adminId);
    List<Contract> findByStatus(String status);
    List<Contract> searchByKeyword(String keyword);
    Double calculateTotalRevenueByAdminId(Long adminId);
} 