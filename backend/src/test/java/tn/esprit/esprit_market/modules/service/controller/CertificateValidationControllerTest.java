package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.esprit_market.modules.service.dto.CertificateValidationDTO;
import tn.esprit.esprit_market.modules.service.service.ICertificateValidationService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateValidationControllerTest {

    @Mock private ICertificateValidationService validationService;
    @InjectMocks private CertificateValidationController controller;
    private CertificateValidationDTO dto;

    @BeforeEach
    void setUp() { dto = new CertificateValidationDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(validationService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<CertificateValidationDTO>> res = controller.getAllValidations();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(validationService.getById(1L)).thenReturn(dto);
        ResponseEntity<CertificateValidationDTO> res = controller.getValidationById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(validationService.create(any(CertificateValidationDTO.class))).thenReturn(dto);
        ResponseEntity<CertificateValidationDTO> res = controller.createValidation(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(validationService.update(eq(1L), any(CertificateValidationDTO.class))).thenReturn(dto);
        ResponseEntity<CertificateValidationDTO> res = controller.updateValidation(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(validationService).delete(1L);
        ResponseEntity<Void> res = controller.deleteValidation(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
