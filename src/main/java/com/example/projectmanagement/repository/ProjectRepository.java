package com.example.projectmanagement.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.projectmanagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(String status);
    List<Project> findBySignedDate(LocalDate signedDate);
    List<Project> findByAdminId(Long adminId); // Thêm method lọc theo adminId
    long countByStatus(String status);
    long countByStatusIn(List<String> statuses);
    List<Project> findByStatusContainingIgnoreCase(String keyword);

    // ✅ Custom truy vấn để lấy N dự án mới nhất
    @Query(value = "SELECT * FROM project ORDER BY signed_date DESC OFFSET 0 ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Project> findTopNByOrderBySignedDateDesc(@org.springframework.data.repository.query.Param("limit") int limit);
    
    // ✅ Custom truy vấn để lấy N dự án mới nhất theo adminId
    @Query(value = "SELECT * FROM project WHERE admin_id = :adminId ORDER BY signed_date DESC OFFSET 0 ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Project> findTopNByAdminIdOrderBySignedDateDesc(@Param("adminId") Long adminId, @Param("limit") int limit);
}
