package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.Project;
import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.repository.ProjectRepository;
import com.example.projectmanagement.service.UserService;
import com.example.projectmanagement.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/home/projects")
public class ProjectController {
    private final ProjectRepository projectRepo;
    private final UserService userService;
    private final PermissionService permissionService;

    public ProjectController(ProjectRepository projectRepo, UserService userService, PermissionService permissionService) {
        this.projectRepo = projectRepo;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public String listProjects(Model model) {
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }

        List<Project> projects = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin thấy dự án của mình
                projects = projectRepo.findByAdminId(loggedInUser.getId());
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ thấy dự án nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewProject")) {
                    Staff staff = permissionService.getStaffForUser(loggedInUser);
                    if (staff != null) {
                        projects = projectRepo.findByAdminId(staff.getAdminId());
                    }
                }
            }
        } else {
            // Nếu chưa đăng nhập thì redirect về login
            return "redirect:/login";
        }

        long activeCount = projects.stream()
                .filter(p -> p.getStatus() != null && p.getStatus().trim().equalsIgnoreCase("Đang thực hiện"))
                .count();
        System.out.println("Số dự án đang thực hiện: " + activeCount);
        model.addAttribute("projects", projects);
        model.addAttribute("status", null);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("loggedInUser", loggedInUser);
        
        // Thêm thông tin quyền để hiển thị/ẩn nút
        boolean canEditProject = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditProject");
        model.addAttribute("canEditProject", canEditProject);

        return "projects/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Kiểm tra quyền chỉnh sửa dự án
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditProject")) {
            return "redirect:/error/access-denied?resource=project";
        }
        
        model.addAttribute("project", new Project());
        return "projects/form";
    }

    @PostMapping
    public String createProject(@ModelAttribute @Valid Project project,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "projects/form";
        }

//         Tự động set adminId nếu chưa có
         if (project.getAdminId() == null) {
             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
             if (authentication != null && authentication.isAuthenticated() &&
                 !"anonymousUser".equals(authentication.getName())) {
                 User loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                 if (loggedInUser != null) {
                     project.setAdminId(loggedInUser.getId());
                     System.out.println("=== DEBUG PROJECT CONTROLLER ===");
                     System.out.println("User ID: " + loggedInUser.getId());
                     System.out.println("Project adminId set to: " + project.getAdminId());
                     System.out.println("=== END DEBUG ===");
                 }
             }
         }

        projectRepo.save(project);
        return "redirect:/home/projects";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam String status, Model model) {
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }

        List<Project> filteredProjects = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin lọc dự án của mình
                List<Project> allProjects = projectRepo.findByAdminId(loggedInUser.getId());
                filteredProjects = allProjects.stream()
                        .filter(p -> status.equals(p.getStatus()))
                        .toList();
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ lọc dự án nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewProject")) {
                    Staff staff = permissionService.getStaffForUser(loggedInUser);
                    if (staff != null) {
                        List<Project> allProjects = projectRepo.findByAdminId(staff.getAdminId());
                        filteredProjects = allProjects.stream()
                                .filter(p -> status.equals(p.getStatus()))
                                .toList();
                    }
                }
            }
        }

        model.addAttribute("projects", filteredProjects);
        model.addAttribute("status", status);
        model.addAttribute("loggedInUser", loggedInUser);
        
        // Thêm thông tin quyền để hiển thị/ẩn nút
        boolean canEditProject = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditProject");
        model.addAttribute("canEditProject", canEditProject);
        return "projects/list";
    }

    @GetMapping("/by-date")
    @ResponseBody
    public List<Project> getProjectsBySignedDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return projectRepo.findBySignedDate(date);
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable Long id, Model model) {
        // Kiểm tra quyền chỉnh sửa dự án
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditProject")) {
            return "redirect:/error/access-denied?resource=project";
        }
        
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("không tìm thấy ID: " + id));
        model.addAttribute("project", project);
        return "projects/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        // Kiểm tra quyền chỉnh sửa dự án (quyền xóa = quyền chỉnh sửa)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditProject")) {
            return "redirect:/error/access-denied?resource=project";
        }
        
        projectRepo.deleteById(id);
        return "redirect:/home/projects";
    }

    @GetMapping("/{id}")
    public String viewProjectDetail(@PathVariable Long id, Model model) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án ID: " + id));
        model.addAttribute("project", project);
        return "projects/detail";
    }
}
