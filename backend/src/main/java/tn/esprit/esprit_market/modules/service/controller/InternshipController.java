package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.InternshipDTO;
import tn.esprit.esprit_market.modules.service.service.IInternshipService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final IInternshipService internshipService;
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<List<InternshipDTO>> getAllInternships() {
        return ResponseEntity.ok(internshipService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipDTO> getInternshipById(@PathVariable Long id) {
        return ResponseEntity.ok(internshipService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<InternshipDTO> createInternship(
            @Valid @RequestBody InternshipDTO internshipDTO,
            Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        internshipDTO.setCreatorId(currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(internshipService.create(internshipDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<InternshipDTO> updateInternship(@PathVariable Long id,
            @Valid @RequestBody InternshipDTO internshipDTO) {
        return ResponseEntity.ok(internshipService.update(id, internshipDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        internshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}