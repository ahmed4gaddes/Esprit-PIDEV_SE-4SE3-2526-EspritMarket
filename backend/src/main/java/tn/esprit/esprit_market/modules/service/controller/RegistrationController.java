package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;
import tn.esprit.esprit_market.modules.service.service.IRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAll());
    }

    /** Must be before `/{id}` so "me" is not parsed as a Long. */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<RegistrationDTO>> getMyRegistrations(Authentication authentication) {
        return ResponseEntity.ok(registrationService.getByUserEmail(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getById(id));
    }

    @GetMapping("/workshop/{workshopId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByWorkshop(@PathVariable Long workshopId) {
        return ResponseEntity.ok(registrationService.getByWorkshopId(workshopId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY', 'ROLE_CUSTOMER')")
    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@Valid @RequestBody RegistrationDTO registrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.create(registrationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationDTO> updateRegistration(@PathVariable Long id,
            @Valid @RequestBody RegistrationDTO registrationDTO) {
        return ResponseEntity.ok(registrationService.update(id, registrationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}