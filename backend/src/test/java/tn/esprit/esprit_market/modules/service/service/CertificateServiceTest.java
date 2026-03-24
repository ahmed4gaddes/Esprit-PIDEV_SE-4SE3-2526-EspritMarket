package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CertificateDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private CertificateService certificateService;

    private Certificate certificate;
    private CertificateDTO certificateDTO;
    private User creator;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);

        certificate = new Certificate();
        certificate.setId(1L);
        certificate.setTitle("Test Certificate");
        certificate.setCreator(creator);

        certificateDTO = new CertificateDTO();
        certificateDTO.setId(1L);
        certificateDTO.setTitle("Test Certificate");
        certificateDTO.setCreatorId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfCertificateDTOs() {
        when(certificateRepository.findAll()).thenReturn(Arrays.asList(certificate));
        when(mapper.toDto(any(Certificate.class))).thenReturn(certificateDTO);

        List<CertificateDTO> result = certificateService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(certificateRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnCertificateDTO() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(mapper.toDto(any(Certificate.class))).thenReturn(certificateDTO);

        CertificateDTO result = certificateService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(certificateRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowResourceNotFoundException() {
        when(certificateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> certificateService.getById(99L));
    }

    @Test
    void create_WhenCreatorExists_ShouldSaveAndReturnCertificateDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(certificateRepository.save(any(Certificate.class))).thenReturn(certificate);
        when(mapper.toDto(any(Certificate.class))).thenReturn(certificateDTO);

        CertificateDTO result = certificateService.create(certificateDTO);

        assertNotNull(result);
        verify(certificateRepository, times(1)).save(any(Certificate.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteCertificate() {
        when(certificateRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> certificateService.delete(1L));
        verify(certificateRepository, times(1)).deleteById(1L);
    }
}
