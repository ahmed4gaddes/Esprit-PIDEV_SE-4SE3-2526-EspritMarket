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
import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;
import tn.esprit.esprit_market.modules.service.service.IRegistrationService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock private IRegistrationService registrationService;
    @Mock private Authentication authentication;
    @InjectMocks private RegistrationController controller;
    private RegistrationDTO dto;

    @BeforeEach
    void setUp() { dto = new RegistrationDTO(); dto.setId(1L); }

    @Test void testGetAll() {
        when(registrationService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RegistrationDTO>> res = controller.getAllRegistrations();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetMyRegistrations() {
        when(authentication.getName()).thenReturn("user@mail.com");
        when(registrationService.getByUserEmail("user@mail.com")).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RegistrationDTO>> res = controller.getMyRegistrations(authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(registrationService.getById(1L)).thenReturn(dto);
        ResponseEntity<RegistrationDTO> res = controller.getRegistrationById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetByWorkshop() {
        when(registrationService.getByWorkshopId(1L)).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<RegistrationDTO>> res = controller.getRegistrationsByWorkshop(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(registrationService.create(any(RegistrationDTO.class))).thenReturn(dto);
        ResponseEntity<RegistrationDTO> res = controller.createRegistration(dto);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(registrationService.update(eq(1L), any(RegistrationDTO.class))).thenReturn(dto);
        ResponseEntity<RegistrationDTO> res = controller.updateRegistration(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(registrationService).delete(1L);
        ResponseEntity<Void> res = controller.deleteRegistration(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
