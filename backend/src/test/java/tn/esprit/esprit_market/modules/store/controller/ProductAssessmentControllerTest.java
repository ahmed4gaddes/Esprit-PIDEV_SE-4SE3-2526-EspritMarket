package tn.esprit.esprit_market.modules.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import tn.esprit.esprit_market.modules.store.entity.ProductAssessment;
import tn.esprit.esprit_market.modules.store.service.ProductAssessmentService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAssessmentControllerTest {

    @Mock
    private ProductAssessmentService assessmentService;

    @Mock
    private IUserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProductAssessmentController controller;

    private User customerUser;
    private User sellerUser;
    private ProductAssessment assessment;

    @BeforeEach
    void setUp() {
        customerUser = new User();
        customerUser.setId(1L);
        customerUser.setEmail("customer@mail.com");
        customerUser.setRole(Role.CUSTOMER);

        sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setEmail("seller@esprit.tn");
        sellerUser.setRole(Role.SELLER);

        assessment = ProductAssessment.builder()
                .id(1L)
                .star(5)
                .comment("Excellent produit!")
                .build();
    }

    @Test
    void testAddAssessment_Success() {
        when(authentication.getName()).thenReturn("customer@mail.com");
        when(userService.getUserByEmail("customer@mail.com")).thenReturn(customerUser);
        when(assessmentService.addAssessment(eq(1L), eq(1L), any(ProductAssessment.class))).thenReturn(assessment);

        ResponseEntity<ProductAssessment> res = controller.addAssessment(1L, assessment, authentication);

        assertEquals(HttpStatus.CREATED, res.getStatusCode());
        assertNotNull(res.getBody());
        assertEquals(5, res.getBody().getStar());
    }

    @Test
    void testAddAssessment_NonCustomer_ThrowsAccessDenied() {
        when(authentication.getName()).thenReturn("seller@esprit.tn");
        when(userService.getUserByEmail("seller@esprit.tn")).thenReturn(sellerUser);

        assertThrows(AccessDeniedException.class, () ->
                controller.addAssessment(1L, assessment, authentication));
    }

    @Test
    void testGetById() {
        when(assessmentService.getById(1L)).thenReturn(assessment);

        ResponseEntity<ProductAssessment> res = controller.getById(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1L, res.getBody().getId());
    }

    @Test
    void testGetByProduct() {
        when(assessmentService.getAssessmentsByProduct(1L)).thenReturn(Arrays.asList(assessment));

        ResponseEntity<List<ProductAssessment>> res = controller.getByProduct(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testGetAverageByProduct() {
        when(assessmentService.getAverageByProduct(1L)).thenReturn(4.5);
        when(assessmentService.getCountByProduct(1L)).thenReturn(10L);

        ResponseEntity<Map<String, Object>> res = controller.getAverageByProduct(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(4.5, res.getBody().get("average"));
        assertEquals(10L, res.getBody().get("count"));
    }

    @Test
    void testGetByUser() {
        when(assessmentService.getAssessmentsByUser(1L)).thenReturn(Arrays.asList(assessment));

        ResponseEntity<List<ProductAssessment>> res = controller.getByUser(1L);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(1, res.getBody().size());
    }

    @Test
    void testUpdate() {
        when(assessmentService.updateAssessment(eq(1L), any(ProductAssessment.class))).thenReturn(assessment);

        ResponseEntity<ProductAssessment> res = controller.update(1L, assessment);

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    void testDelete() {
        doNothing().when(assessmentService).deleteAssessment(1L);

        ResponseEntity<Void> res = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        verify(assessmentService).deleteAssessment(1L);
    }
}
