package com.example.projectmanagement.controller;

import com.example.projectmanagement.entity.Contract;
import com.example.projectmanagement.entity.Staff;
import com.example.projectmanagement.entity.User;
import com.example.projectmanagement.service.ContractService;
import com.example.projectmanagement.service.UserService;
import com.example.projectmanagement.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/home/contracts")
public class ContractController {
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    public String listContracts(Model model) {
        try {
            System.out.println("=== DEBUG LIST CONTRACTS METHOD CALLED ===");
            
            // Lấy user đăng nhập hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedInUser = null;
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
                loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
            }

            List<Contract> contractList;

            System.out.println("=== DEBUG CONTRACT LIST ===");
            System.out.println("Logged in user: " + (loggedInUser != null ? loggedInUser.getTenDangNhap() : "null"));
            System.out.println("User ID: " + (loggedInUser != null ? loggedInUser.getId() : "null"));
            System.out.println("User Role: " + (loggedInUser != null ? loggedInUser.getVaiTro() : "null"));

            if (loggedInUser != null) {
                if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                    // Admin thấy hợp đồng của mình
                    contractList = contractService.findByAdminId(loggedInUser.getId());
                    System.out.println("Found " + contractList.size() + " contracts for admin ID: " + loggedInUser.getId());
                } else if ("USER".equals(loggedInUser.getVaiTro())) {
                    // User chỉ thấy hợp đồng nếu có quyền xem
                    if (permissionService.hasPermission(loggedInUser, "canViewContract")) {
                        Staff staff = permissionService.getStaffForUser(loggedInUser);
                        if (staff != null) {
                            contractList = contractService.findByAdminId(staff.getAdminId());
                            System.out.println("Found " + contractList.size() + " contracts for staff admin ID: " + staff.getAdminId());
                        } else {
                            contractList = List.of();
                        }
                    } else {
                        contractList = List.of();
                    }
                } else {
                    contractList = List.of();
                }
            } else {
                // Nếu chưa đăng nhập thì redirect về login
                System.out.println("No user logged in, redirecting to login");
                return "redirect:/login";
            }
            System.out.println("=== END DEBUG ===");

            // Đếm số lượng theo trạng thái
            long pendingCount = contractList.stream()
                    .filter(c -> "Chờ duyệt".equalsIgnoreCase(c.getStatus()))
                    .count();
            long activeCount = contractList.stream()
                    .filter(c -> "Đang thực hiện".equalsIgnoreCase(c.getStatus()))
                    .count();
            long completedCount = contractList.stream()
                    .filter(c -> "Hoàn thành".equalsIgnoreCase(c.getStatus()))
                    .count();

            model.addAttribute("contractList", contractList);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("loggedInUser", loggedInUser);
            
            // Thêm thông tin quyền để hiển thị/ẩn nút
            boolean canEditContract = loggedInUser != null && permissionService.hasPermission(loggedInUser, "canEditContract");
            model.addAttribute("canEditContract", canEditContract);
            return "contracts/contractlist";
        } catch (Exception e) {
            System.out.println("=== DEBUG CONTRACT CONTROLLER ERROR ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=== END DEBUG ===");
            throw e;
        }
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        System.out.println("=== DEBUG SHOW ADD CONTRACT FORM ===");
        
        // Kiểm tra quyền chỉnh sửa hợp đồng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditContract")) {
            return "redirect:/error/access-denied?resource=contract";
        }
        
        model.addAttribute("contract", new Contract());
        return "contracts/contractform";
    }

    @PostMapping
    public String saveContract(@ModelAttribute @Valid Contract contract, BindingResult result, Model model) {
        System.out.println("=== DEBUG SAVE CONTRACT ===");
        System.out.println("Contract name: " + contract.getContractName());
        System.out.println("Company name: " + contract.getCompanyName());
        System.out.println("Admin ID before save: " + contract.getAdminId());
        
        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            return "contracts/contractform";
        }

        // Tự động set adminId nếu chưa có
        if (contract.getAdminId() == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getName())) {
                User loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
                if (loggedInUser != null) {
                    contract.setAdminId(loggedInUser.getId());
                    System.out.println("User ID: " + loggedInUser.getId());
                    System.out.println("Contract adminId set to: " + contract.getAdminId());
                }
            }
        }

        contractService.save(contract);
        System.out.println("Contract saved with ID: " + contract.getId());
        System.out.println("=== END DEBUG ===");
        
        return "redirect:/home/contracts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        System.out.println("=== DEBUG SHOW EDIT CONTRACT FORM ===");
        System.out.println("Contract ID: " + id);
        
        // Kiểm tra quyền chỉnh sửa hợp đồng
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditContract")) {
            return "redirect:/error/access-denied?resource=contract";
        }
        
        Contract contract = contractService.findById(id);
        if (contract == null) {
            return "redirect:/home/contracts";
        }
        
        model.addAttribute("contract", contract);
        return "contracts/contractform";
    }

    @PostMapping("/update/{id}")
    public String updateContract(@PathVariable Long id, @ModelAttribute @Valid Contract contract, BindingResult result) {
        System.out.println("=== DEBUG UPDATE CONTRACT ===");
        System.out.println("Contract ID: " + id);
        
        if (result.hasErrors()) {
            return "contracts/contractform";
        }

        Contract existingContract = contractService.findById(id);
        if (existingContract != null) {
            contract.setId(id);
            contract.setCreateAt(existingContract.getCreateAt());
            contractService.save(contract);
        }
        
        System.out.println("=== END DEBUG ===");
        return "redirect:/home/contracts";
    }

    @GetMapping("/detail/{id}")
    public String viewContractDetail(@PathVariable Long id, Model model) {
        System.out.println("=== DEBUG VIEW CONTRACT DETAIL ===");
        System.out.println("Contract ID: " + id);
        
        Contract contract = contractService.findById(id);
        if (contract == null) {
            return "redirect:/home/contracts";
        }
        
        model.addAttribute("contract", contract);
        System.out.println("=== END DEBUG ===");
        return "contracts/detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteContract(@PathVariable Long id) {
        System.out.println("=== DEBUG DELETE CONTRACT ===");
        System.out.println("Contract ID: " + id);
        
        // Kiểm tra quyền chỉnh sửa hợp đồng (quyền xóa = quyền chỉnh sửa)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }
        
        if (loggedInUser != null && !permissionService.hasPermission(loggedInUser, "canEditContract")) {
            return "redirect:/error/access-denied?resource=contract";
        }
        
        contractService.deleteById(id);
        System.out.println("=== END DEBUG ===");
        return "redirect:/home/contracts";
    }

    @GetMapping("/filter")
    public String filterByStatus(@RequestParam String status, Model model) {
        System.out.println("=== DEBUG FILTER CONTRACTS ===");
        System.out.println("Status: " + status);
        
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }

        List<Contract> contractList = List.of();
        if (loggedInUser != null) {
            if ("ADMIN".equals(loggedInUser.getVaiTro())) {
                // Admin lọc hợp đồng của mình
                List<Contract> allContracts = contractService.findByAdminId(loggedInUser.getId());
                contractList = allContracts.stream()
                        .filter(c -> status.equals(c.getStatus()))
                        .toList();
            } else if ("USER".equals(loggedInUser.getVaiTro())) {
                // User chỉ lọc hợp đồng nếu có quyền xem
                if (permissionService.hasPermission(loggedInUser, "canViewContract")) {
                    Staff staff = permissionService.getStaffForUser(loggedInUser);
                    if (staff != null) {
                        List<Contract> allContracts = contractService.findByAdminId(staff.getAdminId());
                        contractList = allContracts.stream()
                                .filter(c -> status.equals(c.getStatus()))
                                .toList();
                    }
                }
            }
        }

        // Đếm số lượng theo trạng thái
        long pendingCount = contractList.stream()
                .filter(c -> "Chờ duyệt".equalsIgnoreCase(c.getStatus()))
                .count();
        long activeCount = contractList.stream()
                .filter(c -> "Đang thực hiện".equalsIgnoreCase(c.getStatus()))
                .count();
        long completedCount = contractList.stream()
                .filter(c -> "Hoàn thành".equalsIgnoreCase(c.getStatus()))
                .count();

        model.addAttribute("contractList", contractList);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("selectedStatus", status);
        
        System.out.println("=== END DEBUG ===");
        return "contracts/contractlist";
    }

    @GetMapping("/search")
    public String searchContracts(@RequestParam String keyword, Model model) {
        System.out.println("=== DEBUG SEARCH CONTRACTS ===");
        System.out.println("Keyword: " + keyword);
        
        // Lấy user đăng nhập hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getName())) {
            loggedInUser = userService.getUserByTenDangNhap(authentication.getName());
        }

        List<Contract> contractList;
        if (loggedInUser != null) {
            // Tìm kiếm theo keyword và adminId
            List<Contract> allContracts = contractService.findByAdminId(loggedInUser.getId());
            contractList = allContracts.stream()
                    .filter(c -> c.getContractName() != null && c.getContractName().toLowerCase().contains(keyword.toLowerCase()) ||
                                c.getCompanyName() != null && c.getCompanyName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        } else {
            contractList = List.of();
        }

        // Đếm số lượng theo trạng thái
        long pendingCount = contractList.stream()
                .filter(c -> "Chờ duyệt".equalsIgnoreCase(c.getStatus()))
                .count();
        long activeCount = contractList.stream()
                .filter(c -> "Đang thực hiện".equalsIgnoreCase(c.getStatus()))
                .count();
        long completedCount = contractList.stream()
                .filter(c -> "Hoàn thành".equalsIgnoreCase(c.getStatus()))
                .count();

        model.addAttribute("contractList", contractList);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("searchKeyword", keyword);
        
        System.out.println("=== END DEBUG ===");
        return "contracts/contractlist";
    }
} 