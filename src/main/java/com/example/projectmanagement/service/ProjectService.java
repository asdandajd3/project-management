package com.example.projectmanagement.service;

import com.example.projectmanagement.entity.Project;
import com.example.projectmanagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // ✅ Đếm số dự án theo trạng thái (ví dụ: "Đang thực hiện")
    public long countByStatus(String status) {
        return projectRepository.countByStatus(status);
    }

    // ✅ Tìm N dự án gần đây nhất theo ngày ký (hoặc ID giảm dần)
    public List<Project> findRecentProjects(int limit) {
        return projectRepository.findTopNByOrderBySignedDateDesc(limit); // sẽ viết hàm custom ở dưới
    }
    
    // ✅ Tìm dự án theo adminId
    public List<Project> findByAdminId(Long adminId) {
        return projectRepository.findByAdminId(adminId);
    }
    
    // ✅ Tìm N dự án gần đây nhất theo adminId
    public List<Project> findRecentProjectsByAdminId(Long adminId, int limit) {
        return projectRepository.findTopNByAdminIdOrderBySignedDateDesc(adminId, limit);
    }
}
