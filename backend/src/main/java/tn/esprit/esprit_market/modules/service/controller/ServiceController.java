package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;
import tn.esprit.esprit_market.modules.service.service.IServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final IServiceService baseServiceService;

    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getAllServices() {
        return ResponseEntity.ok(baseServiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(baseServiceService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceDTO> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDTO serviceDTO) {
        return ResponseEntity.ok(baseServiceService.update(id, serviceDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        baseServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}