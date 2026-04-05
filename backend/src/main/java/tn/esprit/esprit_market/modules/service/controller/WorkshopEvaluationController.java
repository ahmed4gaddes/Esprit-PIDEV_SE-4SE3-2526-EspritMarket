package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.WorkshopEvaluationDTO;
import tn.esprit.esprit_market.modules.service.service.IWorkshopEvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class WorkshopEvaluationController {

    private final IWorkshopEvaluationService evaluationService;

    @GetMapping
    public ResponseEntity<List<WorkshopEvaluationDTO>> getAllEvaluations() {
        return ResponseEntity.ok(evaluationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshopEvaluationDTO> getEvaluationById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<WorkshopEvaluationDTO> createEvaluation(
            @Valid @RequestBody WorkshopEvaluationDTO evaluationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluationService.create(evaluationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<WorkshopEvaluationDTO> updateEvaluation(@PathVariable Long id,
            @Valid @RequestBody WorkshopEvaluationDTO evaluationDTO) {
        return ResponseEntity.ok(evaluationService.update(id, evaluationDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        evaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}