package com.example.projectmanagement.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String avatar;
    private String fullName;
    private String email;
    private String position;
    private int salary;
    private String startDate;
    private String status; // Hoạt động, Nghỉ phép, Thực tập
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "can_edit_project")
    private Boolean canEditProject;

    @Column(name = "can_view_project")
    private Boolean canViewProject;

    @Column(name = "can_view_contract")
    private Boolean canViewContract;

    @Column(name = "can_edit_contract")
    private Boolean canEditContract;

    @Column(name = "can_view_staff")
    private Boolean canViewStaff;

    @Column(name = "can_edit_staff")
    private Boolean canEditStaff;

    @Column(name = "admin_id")
    private Long adminId;

    public Staff() {
    }

    public Staff(Long id, String fullName, String email, String position, int salary, String startDate, String status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.position = position;
        this.salary = salary;
        this.startDate = startDate;
        this.status = status;
    }

    // Getters và Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCanViewProject() {
        return Boolean.TRUE.equals(canViewProject);
    }

    public void setCanViewProject(boolean canViewProject) {
        this.canViewProject = canViewProject;
    }

    public boolean isCanEditProject() {
        return Boolean.TRUE.equals(canEditProject);
    }

    public void setCanEditProject(boolean canEditProject) {
        this.canEditProject = canEditProject;
    }

    public boolean isCanViewContract() {
        return Boolean.TRUE.equals(canViewContract);
    }

    public void setCanViewContract(boolean canViewContract) {
        this.canViewContract = canViewContract;
    }

    public boolean isCanEditContract() {
        return Boolean.TRUE.equals(canEditContract);
    }

    public void setCanEditContract(boolean canEditContract) {
        this.canEditContract = canEditContract;
    }

    public boolean isCanViewStaff() {
        return Boolean.TRUE.equals(canViewStaff);
    }

    public void setCanViewStaff(boolean canViewStaff) {
        this.canViewStaff = canViewStaff;
    }

    public boolean isCanEditStaff() {
        return Boolean.TRUE.equals(canEditStaff);
    }

    public void setCanEditStaff(boolean canEditStaff) {
        this.canEditStaff = canEditStaff;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
