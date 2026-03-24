package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.CertificateValidationDTO;
import tn.esprit.esprit_market.modules.service.service.ICertificateValidationService;

import java.util.List;

@RestController
@RequestMapping("/api/certificate-validations")
@RequiredArgsConstructor
public class CertificateValidationController {

    private final ICertificateValidationService validationService;

    @GetMapping
    public ResponseEntity<List<CertificateValidationDTO>> getAllValidations() {
        return ResponseEntity.ok(validationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateValidationDTO> getValidationById(@PathVariable Long id) {
        return ResponseEntity.ok(validationService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<CertificateValidationDTO> createValidation(
            @Valid @RequestBody CertificateValidationDTO validationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(validationService.create(validationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<CertificateValidationDTO> updateValidation(@PathVariable Long id,
            @Valid @RequestBody CertificateValidationDTO validationDTO) {
        return ResponseEntity.ok(validationService.update(id, validationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidation(@PathVariable Long id) {
        validationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}