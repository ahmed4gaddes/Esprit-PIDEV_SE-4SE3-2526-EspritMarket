package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CertificateValidationDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.CertificateValidation;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.service.repository.CertificateValidationRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateValidationServiceTest {

    @Mock
    private CertificateValidationRepository validationRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private CertificateValidationService validationService;

    private CertificateValidation validation;
    private CertificateValidationDTO validationDTO;
    private Certificate certificate;
    private User validator;

    @BeforeEach
    void setUp() {
        certificate = new Certificate();
        certificate.setId(1L);

        validator = new User();
        validator.setId(1L);

        validation = new CertificateValidation();
        validation.setId(1L);
        validation.setComment("Approved");
        validation.setCertificate(certificate);
        validation.setValidator(validator);

        validationDTO = new CertificateValidationDTO();
        validationDTO.setId(1L);
        validationDTO.setComment("Approved");
        validationDTO.setCertificateId(1L);
        validationDTO.setValidatorId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfValidationDTOs() {
        when(validationRepository.findAll()).thenReturn(Arrays.asList(validation));
        when(mapper.toDto(any(CertificateValidation.class))).thenReturn(validationDTO);

        List<CertificateValidationDTO> result = validationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnValidationDTO() {
        when(validationRepository.findById(1L)).thenReturn(Optional.of(validation));
        when(mapper.toDto(any(CertificateValidation.class))).thenReturn(validationDTO);

        CertificateValidationDTO result = validationService.getById(1L);

        assertNotNull(result);
        assertEquals("Approved", result.getComment());
    }

    @Test
    void create_WhenCertificateAndValidatorExist_ShouldSaveAndReturnValidationDTO() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(userRepository.findById(1L)).thenReturn(Optional.of(validator));
        when(validationRepository.save(any(CertificateValidation.class))).thenReturn(validation);
        when(mapper.toDto(any(CertificateValidation.class))).thenReturn(validationDTO);

        CertificateValidationDTO result = validationService.create(validationDTO);

        assertNotNull(result);
        verify(certificateRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(validationRepository, times(1)).save(any(CertificateValidation.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteValidation() {
        when(validationRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> validationService.delete(1L));
        verify(validationRepository, times(1)).deleteById(1L);
    }
}
