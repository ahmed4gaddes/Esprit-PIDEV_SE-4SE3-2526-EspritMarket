package tn.esprit.esprit_market.modules.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.admin.dto.AdminDashboardResponseDTO;
import tn.esprit.esprit_market.modules.admin.dto.AdminAuditLogDTO;
import tn.esprit.esprit_market.modules.admin.dto.PagedResponseDTO;
import tn.esprit.esprit_market.modules.admin.dto.UpdateUserRoleRequest;
import tn.esprit.esprit_market.modules.admin.service.AdminService;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.user.dto.UserResponseDTO;
import tn.esprit.esprit_market.modules.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardResponseDTO> getDashboard(
            @RequestParam(name = "days", defaultValue = "7") int days) {
        return ResponseEntity.ok(adminService.getDashboard(days));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponseDTO<UserResponseDTO>> getAllUsers(
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PagedResponseDTO<User> pagedUsers = adminService.getUsers(role, active, q, page, size);
        List<UserResponseDTO> users = pagedUsers.getItems()
                .stream()
                .map(this::mapUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponseDTO.<UserResponseDTO>builder()
                .items(users)
                .page(pagedUsers.getPage())
                .size(pagedUsers.getSize())
                .totalElements(pagedUsers.getTotalElements())
                .totalPages(pagedUsers.getTotalPages())
                .hasNext(pagedUsers.isHasNext())
                .hasPrevious(pagedUsers.isHasPrevious())
                .build());
    }

    @PutMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(mapUser(adminService.toggleUserStatus(id, authentication.getName())));
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(mapUser(adminService.updateUserRole(id, request.getRole(), authentication.getName())));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        adminService.deleteUser(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internship-applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponseDTO<InternshipApplicationDTO>> getInternshipApplications(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "internshipId", required = false) Long internshipId,
            @RequestParam(name = "companyId", required = false) Long companyId,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getInternshipApplications(status, internshipId, companyId, q, page, size));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponseDTO<AdminAuditLogDTO>> getAuditLogs(
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getAuditLogs(action, q, page, size));
    }

    private UserResponseDTO mapUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .storeActive(user.isStoreActive())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .profilePicture(user.getProfilePicture())
                .totalSales(user.getTotalSales())
                .dateOfBirth(user.getDateOfBirth() != null
                        ? new java.sql.Date(user.getDateOfBirth().getTime()).toLocalDate()
                        : null)
                .createdAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .isActive(user.isActive())
                .build();
    }
}
