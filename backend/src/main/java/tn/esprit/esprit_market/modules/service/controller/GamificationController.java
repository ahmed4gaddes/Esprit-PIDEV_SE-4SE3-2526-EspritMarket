package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.GamificationDTO;
import tn.esprit.esprit_market.modules.service.service.IGamificationService;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final IGamificationService gamificationService;

    @GetMapping
    public ResponseEntity<List<GamificationDTO>> getAllGamifications() {
        return ResponseEntity.ok(gamificationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamificationDTO> getGamificationById(@PathVariable Long id) {
        return ResponseEntity.ok(gamificationService.getById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<GamificationDTO> getByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(gamificationService.getByUserId(id)); // Fixed semantic bug here
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<GamificationDTO> createGamification(@Valid @RequestBody GamificationDTO gamificationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gamificationService.create(gamificationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<GamificationDTO> updateGamification(@PathVariable Long id,
            @Valid @RequestBody GamificationDTO gamificationDTO) {
        return ResponseEntity.ok(gamificationService.update(id, gamificationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGamification(@PathVariable Long id) {
        gamificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}