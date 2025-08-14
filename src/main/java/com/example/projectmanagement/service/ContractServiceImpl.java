package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Contract;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private UserService userService;
    
    @Override
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }
    
    @Override
    public Contract findById(Long id) {
        return contractRepository.findById(id).orElse(null);
    }
    
    @Override
    public void save(Contract contract) {
        // Tự động set adminId nếu chưa có
        if (contract.getAdminId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
                User loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                if (loggedInUser != null) {
                    contract.setAdminId(loggedInUser.getId());
                    System.out.println("=== DEBUG CONTRACT SERVICE ===");
                    System.out.println("User ID: " + loggedInUser.getId());
                    System.out.println("Contract adminId set to: " + contract.getAdminId());
                    System.out.println("=== END DEBUG ===");
                }
            }
        }
        
        // Tự động set thời gian
        if (contract.getId() == null) {
            contract.setCreateAt(LocalDateTime.now());
        }
        contract.setUpdateAt(LocalDateTime.now());
        
        contractRepository.save(contract);
    }
    
    @Override
    public void deleteById(Long id) {
        contractRepository.deleteById(id);
    }
    
    @Override
    public List<Contract> findByAdminId(Long adminId) {
        return contractRepository.findByAdminId(adminId);
    }
    
    @Override
    public List<Contract> findByStatus(String status) {
        return contractRepository.findByStatus(status);
    }
    
    @Override
    public List<Contract> searchByKeyword(String keyword) {
        return contractRepository.findByStatusContainingIgnoreCase(keyword);
    }
    
    @Override
    public Double calculateTotalRevenueByAdminId(Long adminId) {
        List<Contract> contracts = contractRepository.findByAdminId(adminId);
        return contracts.stream()
                .filter(contract -> contract.getValue() != null)
                .mapToDouble(Contract::getValue)
                .sum();
    }
} 