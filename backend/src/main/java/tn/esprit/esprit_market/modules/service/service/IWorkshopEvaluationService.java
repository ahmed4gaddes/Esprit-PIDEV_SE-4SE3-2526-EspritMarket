package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.WorkshopEvaluationDTO;

import java.util.List;

public interface IWorkshopEvaluationService {
    List<WorkshopEvaluationDTO> getAll();

    WorkshopEvaluationDTO getById(Long id);

    WorkshopEvaluationDTO create(WorkshopEvaluationDTO dto);

    WorkshopEvaluationDTO update(Long id, WorkshopEvaluationDTO dto);

    void delete(Long id);
}
