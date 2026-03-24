package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.WorkshopEvaluationDTO;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.WorkshopEvaluation;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopEvaluationRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkshopEvaluationService implements IWorkshopEvaluationService {
    private final WorkshopEvaluationRepository evaluationRepository;
    private final RegistrationRepository registrationRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<WorkshopEvaluationDTO> getAll() {
        return evaluationRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkshopEvaluationDTO getById(Long id) {
        WorkshopEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + id));
        return mapper.toDto(evaluation);
    }

    public WorkshopEvaluationDTO create(WorkshopEvaluationDTO dto) {
        WorkshopEvaluation evaluation = new WorkshopEvaluation();
        mapper.toEntity(dto, evaluation);

        evaluation.setEvaluationDate(new Date());

        if (dto.getRegistrationId() != null) {
            Registration registration = registrationRepository.findById(dto.getRegistrationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Registration not found with id: " + dto.getRegistrationId()));
            evaluation.setRegistration(registration);
        }

        return mapper.toDto(evaluationRepository.save(evaluation));
    }

    public WorkshopEvaluationDTO update(Long id, WorkshopEvaluationDTO dto) {
        WorkshopEvaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation not found with id: " + id));

        mapper.toEntity(dto, evaluation);

        if (dto.getRegistrationId() != null && (evaluation.getRegistration() == null
                || !evaluation.getRegistration().getId().equals(dto.getRegistrationId()))) {
            Registration registration = registrationRepository.findById(dto.getRegistrationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Registration not found with id: " + dto.getRegistrationId()));
            evaluation.setRegistration(registration);
        }

        return mapper.toDto(evaluationRepository.save(evaluation));
    }

    public void delete(Long id) {
        if (!evaluationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Evaluation not found with id: " + id);
        }
        evaluationRepository.deleteById(id);
    }
}