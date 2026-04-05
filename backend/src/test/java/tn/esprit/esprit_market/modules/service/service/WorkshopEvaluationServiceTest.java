package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.WorkshopEvaluationDTO;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.WorkshopEvaluation;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;
import tn.esprit.esprit_market.modules.service.repository.WorkshopEvaluationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopEvaluationServiceTest {

    @Mock
    private WorkshopEvaluationRepository evaluationRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private WorkshopEvaluationService evaluationService;

    private WorkshopEvaluation evaluation;
    private WorkshopEvaluationDTO evaluationDTO;
    private Registration registration;

    @BeforeEach
    void setUp() {
        registration = new Registration();
        registration.setId(1L);

        evaluation = new WorkshopEvaluation();
        evaluation.setId(1L);
        evaluation.setRating(5);
        evaluation.setRegistration(registration);

        evaluationDTO = new WorkshopEvaluationDTO();
        evaluationDTO.setId(1L);
        evaluationDTO.setRating(5);
        evaluationDTO.setRegistrationId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfWorkshopEvaluationDTOs() {
        when(evaluationRepository.findAll()).thenReturn(Arrays.asList(evaluation));
        when(mapper.toDto(any(WorkshopEvaluation.class))).thenReturn(evaluationDTO);

        List<WorkshopEvaluationDTO> result = evaluationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnWorkshopEvaluationDTO() {
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));
        when(mapper.toDto(any(WorkshopEvaluation.class))).thenReturn(evaluationDTO);

        WorkshopEvaluationDTO result = evaluationService.getById(1L);

        assertNotNull(result);
        assertEquals(5, result.getRating());
    }

    @Test
    void create_WhenRegistrationExists_ShouldSaveAndReturnWorkshopEvaluationDTO() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(evaluationRepository.save(any(WorkshopEvaluation.class))).thenReturn(evaluation);
        when(mapper.toDto(any(WorkshopEvaluation.class))).thenReturn(evaluationDTO);

        WorkshopEvaluationDTO result = evaluationService.create(evaluationDTO);

        assertNotNull(result);
        verify(evaluationRepository, times(1)).save(any(WorkshopEvaluation.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteWorkshopEvaluation() {
        when(evaluationRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> evaluationService.delete(1L));
        verify(evaluationRepository, times(1)).deleteById(1L);
    }
}
