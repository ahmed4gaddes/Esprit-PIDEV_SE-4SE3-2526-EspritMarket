package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.SupportingDocumentDTO;
import tn.esprit.esprit_market.modules.service.service.ISupportingDocumentService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportingDocumentControllerTest {

    @Mock private ISupportingDocumentService documentService;
    @InjectMocks private SupportingDocumentController controller;
    private SupportingDocumentDTO dto;

    @BeforeEach
    void setUp() { dto = new SupportingDocumentDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(documentService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<SupportingDocumentDTO>> res = controller.getAllDocuments();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(documentService.getById(1L)).thenReturn(dto);
        ResponseEntity<SupportingDocumentDTO> res = controller.getDocumentById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(documentService.create(any(SupportingDocumentDTO.class))).thenReturn(dto);
        ResponseEntity<SupportingDocumentDTO> res = controller.createDocument(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(documentService.update(eq(1L), any(SupportingDocumentDTO.class))).thenReturn(dto);
        ResponseEntity<SupportingDocumentDTO> res = controller.updateDocument(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(documentService).delete(1L);
        ResponseEntity<Void> res = controller.deleteDocument(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
