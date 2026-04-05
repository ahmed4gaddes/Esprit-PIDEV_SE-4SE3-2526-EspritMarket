package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;
import tn.esprit.esprit_market.modules.service.service.IWorkshopService;

import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@RequiredArgsConstructor
public class WorkshopController {

    private final IWorkshopService workshopService;

    @GetMapping
    public ResponseEntity<List<WorkshopDTO>> getAllWorkshops() {
        return ResponseEntity.ok(workshopService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshopDTO> getWorkshopById(@PathVariable Long id) {
        return ResponseEntity.ok(workshopService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<WorkshopDTO> createWorkshop(@Valid @RequestBody WorkshopDTO workshopDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workshopService.create(workshopDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<WorkshopDTO> updateWorkshop(@PathVariable Long id,
            @Valid @RequestBody WorkshopDTO workshopDTO) {
        return ResponseEntity.ok(workshopService.update(id, workshopDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkshop(@PathVariable Long id) {
        workshopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}