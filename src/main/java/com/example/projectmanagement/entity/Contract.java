package com.example.projectmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String contractName;
    private String companyName;
    private String description;
    private String currency;
    private Double value;
    private Integer term;
    private String status;
    private LocalDateTime signedDate;
    private LocalDateTime expriryDate;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long adminId; // Thêm thuộc tính adminId
    
    public Contract() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContractName() {
        return contractName;
    }
    
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Double getValue() {
        return value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public Integer getTerm() {
        return term;
    }
    
    public void setTerm(Integer term) {
        this.term = term;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getSignedDate() {
        return signedDate;
    }
    
    public void setSignedDate(LocalDateTime signedDate) {
        this.signedDate = signedDate;
    }
    
    public LocalDateTime getExpriryDate() {
        return expriryDate;
    }
    
    public void setExpriryDate(LocalDateTime expriryDate) {
        this.expriryDate = expriryDate;
    }
    
    public LocalDateTime getCreateAt() {
        return createAt;
    }
    
    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
    
    public LocalDateTime getUpdateAt() {
        return updateAt;
    }
    
    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
} 