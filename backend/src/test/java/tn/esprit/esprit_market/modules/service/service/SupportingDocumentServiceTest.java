package tn.esprit.esprit_market.modules.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.SupportingDocumentDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.SupportingDocument;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.service.repository.SupportingDocumentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportingDocumentServiceTest {

    @Mock
    private SupportingDocumentRepository documentRepository;

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private ServiceModuleMapper mapper;

    @InjectMocks
    private SupportingDocumentService documentService;

    private SupportingDocument document;
    private SupportingDocumentDTO documentDTO;
    private Certificate certificate;

    @BeforeEach
    void setUp() {
        certificate = new Certificate();
        certificate.setId(1L);

        document = new SupportingDocument();
        document.setId(1L);
        document.setName("Test Doc");
        document.setCertificate(certificate);

        documentDTO = new SupportingDocumentDTO();
        documentDTO.setId(1L);
        documentDTO.setName("Test Doc");
        documentDTO.setCertificateId(1L);
    }

    @Test
    void getAll_ShouldReturnListOfSupportingDocumentDTOs() {
        when(documentRepository.findAll()).thenReturn(Arrays.asList(document));
        when(mapper.toDto(any(SupportingDocument.class))).thenReturn(documentDTO);

        List<SupportingDocumentDTO> result = documentService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getById_WhenExists_ShouldReturnSupportingDocumentDTO() {
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(mapper.toDto(any(SupportingDocument.class))).thenReturn(documentDTO);

        SupportingDocumentDTO result = documentService.getById(1L);

        assertNotNull(result);
        assertEquals("Test Doc", result.getName());
    }

    @Test
    void create_WhenCertificateExists_ShouldSaveAndReturnSupportingDocumentDTO() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));
        when(documentRepository.save(any(SupportingDocument.class))).thenReturn(document);
        when(mapper.toDto(any(SupportingDocument.class))).thenReturn(documentDTO);

        SupportingDocumentDTO result = documentService.create(documentDTO);

        assertNotNull(result);
        verify(documentRepository, times(1)).save(any(SupportingDocument.class));
    }

    @Test
    void delete_WhenExists_ShouldDeleteSupportingDocument() {
        when(documentRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> documentService.delete(1L));
        verify(documentRepository, times(1)).deleteById(1L);
    }
}
