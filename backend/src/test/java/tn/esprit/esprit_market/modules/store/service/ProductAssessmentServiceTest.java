package tn.esprit.esprit_market.modules.store.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductAssessment;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.ProductAssessmentRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAssessmentServiceTest {

    @Mock private ProductAssessmentRepository assessmentRepository;
    @Mock private IRepositoryProduct productRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private ProductAssessmentService service;

    private Product product;
    private User user;
    private ProductAssessment assessment;

    @BeforeEach
    void setUp() {
        product = new Product(); product.setId(1L);
        user = new User(); user.setId(1L);
        assessment = new ProductAssessment();
        assessment.setId(1L); assessment.setStar(4); assessment.setComment("Good");
    }

    @Test void testAddAssessment_New() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assessmentRepository.findByUser_IdAndProduct_Id(1L, 1L)).thenReturn(Optional.empty());
        when(assessmentRepository.save(any(ProductAssessment.class))).thenReturn(assessment);

        ProductAssessment result = service.addAssessment(1L, 1L, assessment);
        assertNotNull(result);
    }

    @Test void testAddAssessment_Update() {
        ProductAssessment existing = new ProductAssessment();
        existing.setId(2L); existing.setStar(3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(assessmentRepository.findByUser_IdAndProduct_Id(1L, 1L)).thenReturn(Optional.of(existing));
        when(assessmentRepository.save(any(ProductAssessment.class))).thenReturn(existing);

        ProductAssessment result = service.addAssessment(1L, 1L, assessment);
        assertNotNull(result);
        assertEquals(4, existing.getStar());
    }

    @Test void testGetAssessmentsByProduct() {
        when(assessmentRepository.findByProduct_Id(1L)).thenReturn(Arrays.asList(assessment));
        List<ProductAssessment> result = service.getAssessmentsByProduct(1L);
        assertEquals(1, result.size());
    }

    @Test void testGetAssessmentsByUser() {
        when(assessmentRepository.findByUser_Id(1L)).thenReturn(Arrays.asList(assessment));
        List<ProductAssessment> result = service.getAssessmentsByUser(1L);
        assertEquals(1, result.size());
    }

    @Test void testGetAverageByProduct() {
        when(assessmentRepository.findAverageStarByProduct_Id(1L)).thenReturn(4.5);
        assertEquals(4.5, service.getAverageByProduct(1L));
    }

    @Test void testGetAverageByProduct_Null() {
        when(assessmentRepository.findAverageStarByProduct_Id(1L)).thenReturn(null);
        assertEquals(0.0, service.getAverageByProduct(1L));
    }

    @Test void testGetCountByProduct() {
        when(assessmentRepository.countByProduct_Id(1L)).thenReturn(5L);
        assertEquals(5L, service.getCountByProduct(1L));
    }

    @Test void testUpdateAssessment() {
        ProductAssessment updated = new ProductAssessment();
        updated.setStar(5); updated.setComment("Excellent");
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(assessment));
        when(assessmentRepository.save(any(ProductAssessment.class))).thenReturn(assessment);

        ProductAssessment result = service.updateAssessment(1L, updated);
        assertEquals(5, assessment.getStar());
    }

    @Test void testDeleteAssessment() {
        when(assessmentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(assessmentRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteAssessment(1L));
    }

    @Test void testDeleteAssessment_NotFound() {
        when(assessmentRepository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteAssessment(1L));
    }

    @Test void testGetById() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(assessment));
        assertEquals(assessment, service.getById(1L));
    }
}
