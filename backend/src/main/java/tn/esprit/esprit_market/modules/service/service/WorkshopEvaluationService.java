package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.WorkshopEvaluation;
import tn.esprit.esprit_market.modules.service.repository.WorkshopEvaluationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkshopEvaluationService {
    private final WorkshopEvaluationRepository evaluationRepository;

    public List<WorkshopEvaluation> getAll() {
        return evaluationRepository.findAll();
    }

    public WorkshopEvaluation getById(Long id) {
        return evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
    }

    public WorkshopEvaluation create(WorkshopEvaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    public WorkshopEvaluation update(Long id, WorkshopEvaluation evaluation) {
        if (!evaluationRepository.existsById(id)) {
            throw new RuntimeException("Evaluation not found");
        }
        evaluation.setId(id);
        return evaluationRepository.save(evaluation);
    }

    public void delete(Long id) {
        evaluationRepository.deleteById(id);
    }
}