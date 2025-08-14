package com.example.projectmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotNull;
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String companyName;
    @NotNull(message = "Chi phí không được để trống")
    private Double cost;;
    private Integer duration;
    private LocalDate signedDate;
    public Project() {
    }
    public Project(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getCost() {
        return cost;
    }
    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }
    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Getters & Setters
    // Constructor mặc định & có tham số (nếu cần)
}
