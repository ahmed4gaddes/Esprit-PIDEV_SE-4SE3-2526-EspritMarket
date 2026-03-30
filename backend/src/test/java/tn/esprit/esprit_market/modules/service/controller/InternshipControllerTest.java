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
import tn.esprit.esprit_market.modules.service.dto.InternshipDTO;
import tn.esprit.esprit_market.modules.service.service.IInternshipService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {

    @Mock private IInternshipService internshipService;
    @Mock private IUserService userService;
    @Mock private Authentication authentication;
    @InjectMocks private InternshipController controller;
    private InternshipDTO dto;
    private User user;

    @BeforeEach
    void setUp() {
        dto = new InternshipDTO(); dto.setId(1L);
        user = new User(); user.setId(1L); user.setEmail("expert@mail.com");
    }

    @Test void testGetAll() {
        when(internshipService.getAll()).thenReturn(Arrays.asList(dto));
        ResponseEntity<List<InternshipDTO>> res = controller.getAllInternships();
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testGetById() {
        when(internshipService.getById(1L)).thenReturn(dto);
        ResponseEntity<InternshipDTO> res = controller.getInternshipById(1L);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testCreate() {
        when(authentication.getName()).thenReturn("expert@mail.com");
        when(userService.getUserByEmail("expert@mail.com")).thenReturn(user);
        when(internshipService.create(any(InternshipDTO.class))).thenReturn(dto);
        ResponseEntity<InternshipDTO> res = controller.createInternship(dto, authentication);
        assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test void testUpdate() {
        when(internshipService.update(eq(1L), any(InternshipDTO.class))).thenReturn(dto);
        ResponseEntity<InternshipDTO> res = controller.updateInternship(1L, dto);
        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test void testDelete() {
        doNothing().when(internshipService).delete(1L);
        ResponseEntity<Void> res = controller.deleteInternship(1L);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
    }
}
