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
import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;
import tn.esprit.esprit_market.modules.service.service.IServiceService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock private IServiceService baseServiceService;
    @Mock private Authentication authentication;
    @InjectMocks private ServiceController controller;
    private ServiceDTO serviceDTO;

    @BeforeEach
    void setUp() {
        serviceDTO = new ServiceDTO();
        serviceDTO.setId(1L);
    }

    @Test
    void testGetAllServices() {
        when(baseServiceService.getAll()).thenReturn(Arrays.asList(serviceDTO));
        ResponseEntity<List<ServiceDTO>> res = controller.getAllServices();
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetMyServices() {
        when(authentication.getName()).thenReturn("expert@mail.com");
        when(baseServiceService.getMyServices("expert@mail.com")).thenReturn(Arrays.asList(serviceDTO));
        ResponseEntity<List<ServiceDTO>> res = controller.getMyServices(authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testGetServiceById() {
        when(baseServiceService.getById(1L)).thenReturn(serviceDTO);
        ResponseEntity<ServiceDTO> res = controller.getServiceById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testUpdateService() {
        when(authentication.getName()).thenReturn("expert@mail.com");
        when(baseServiceService.update(eq(1L), any(ServiceDTO.class), eq("expert@mail.com"))).thenReturn(serviceDTO);
        ResponseEntity<ServiceDTO> res = controller.updateService(1L, serviceDTO, authentication);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testDeleteService() {
        when(authentication.getName()).thenReturn("expert@mail.com");
        doNothing().when(baseServiceService).delete(1L, "expert@mail.com");
        ResponseEntity<Void> res = controller.deleteService(1L, authentication);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
