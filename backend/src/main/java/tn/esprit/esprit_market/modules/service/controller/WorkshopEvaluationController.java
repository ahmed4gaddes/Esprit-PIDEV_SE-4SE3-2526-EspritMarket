package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.WorkshopEvaluation;
import tn.esprit.esprit_market.modules.service.service.WorkshopEvaluationService;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkshopEvaluationController {
    private final WorkshopEvaluationService evaluationService;

    @GetMapping
    public List<WorkshopEvaluation> getAll() {
        return evaluationService.getAll();
    }

    @PostMapping
    public ResponseEntity<WorkshopEvaluation> create(@RequestBody WorkshopEvaluation evaluation) {
        return new ResponseEntity<>(evaluationService.create(evaluation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkshopEvaluation> update(@PathVariable Long id, @RequestBody WorkshopEvaluation evaluation) {
        return ResponseEntity.ok(evaluationService.update(id, evaluation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}