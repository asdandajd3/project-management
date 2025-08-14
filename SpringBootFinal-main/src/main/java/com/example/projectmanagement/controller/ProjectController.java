package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.Project;
import com.example.projectmanagement.repository.ProjectRepository;
import jakarta.validation.Valid;
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

    public ProjectController(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @GetMapping
    public String listProjects(Model model) {
        List<Project> projects = projectRepo.findAll();

        long activeCount = projects.stream()
                .filter(p -> p.getStatus() != null && p.getStatus().trim().equalsIgnoreCase("Đang thực hiện"))
                .count();
        System.out.println("Số dự án đang thực hiện: " + activeCount);
        model.addAttribute("projects", projects);
        model.addAttribute("status", null);
        model.addAttribute("activeCount", activeCount);

        return "projects/list";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {
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

        projectRepo.save(project);
        return "redirect:/home/projects";
    }

    @GetMapping("/filter")
    public String filterProjects(@RequestParam String status, Model model) {
        List<Project> filteredProjects = projectRepo.findByStatus(status);
        model.addAttribute("projects", filteredProjects);
        model.addAttribute("status", status);
        return "projects/list";
    }

    @GetMapping("/by-date")
    @ResponseBody
    public List<Project> getProjectsBySignedDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return projectRepo.findBySignedDate(date);
    }

    @GetMapping("/edit/{id}")
    public String editProject(@PathVariable Long id, Model model) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("không tìm thấy ID: " + id));
        model.addAttribute("project", project);
        return "projects/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
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
