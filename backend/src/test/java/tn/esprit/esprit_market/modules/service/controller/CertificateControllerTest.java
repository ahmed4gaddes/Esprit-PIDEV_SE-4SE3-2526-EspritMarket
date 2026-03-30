package tn.esprit.esprit_market.modules.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.service.dto.CertificateDTO;
import tn.esprit.esprit_market.modules.service.dto.CertificateEligibilityDTO;
import tn.esprit.esprit_market.modules.service.service.ICertificateService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateControllerTest {

    @Mock
    private ICertificateService certificateService;

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CertificateController controller;

    private CertificateDTO dto;

    @BeforeEach
    void setUp() {
        dto = new CertificateDTO();
        dto.setId(10L);
        dto.setTitle("Angular Expert");
    }

    @Test
    void testGetAllCertificates() {
        when(certificateService.getAll()).thenReturn(Arrays.asList(dto));

        ResponseEntity<List<CertificateDTO>> response = controller.getAllCertificates();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(certificateService).getAll();
    }

    @Test
    void testGetCertificateById() {
        when(certificateService.getById(10L)).thenReturn(dto);

        ResponseEntity<CertificateDTO> response = controller.getCertificateById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(certificateService).getById(10L);
    }

    @Test
    void testGetEligibility() {
        User user = new User();
        user.setId(5L);
        when(authentication.getName()).thenReturn("test@mail.com");
        when(userService.getUserByEmail("test@mail.com")).thenReturn(user);
        
        CertificateEligibilityDTO elDto = new CertificateEligibilityDTO();
        when(certificateService.getEligibility(10L, 5L)).thenReturn(elDto);

        ResponseEntity<CertificateEligibilityDTO> response = controller.getEligibility(10L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(certificateService).getEligibility(10L, 5L);
    }

    @Test
    void testCreateCertificate() {
        when(certificateService.create(any(CertificateDTO.class))).thenReturn(dto);

        ResponseEntity<CertificateDTO> response = controller.createCertificate(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(10L, response.getBody().getId());
        verify(certificateService).create(any(CertificateDTO.class));
    }

    @Test
    void testUpdateCertificate() {
        when(certificateService.update(eq(10L), any(CertificateDTO.class))).thenReturn(dto);

        ResponseEntity<CertificateDTO> response = controller.updateCertificate(10L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Angular Expert", response.getBody().getTitle());
        verify(certificateService).update(eq(10L), any(CertificateDTO.class));
    }

    @Test
    void testDeleteCertificate() {
        doNothing().when(certificateService).delete(10L);

        ResponseEntity<Void> response = controller.deleteCertificate(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(certificateService).delete(10L);
    }
}
