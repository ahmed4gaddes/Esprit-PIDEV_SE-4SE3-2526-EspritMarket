package tn.esprit.esprit_market.modules.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.ProductAssessment;
import tn.esprit.esprit_market.modules.store.repository.ProductAssessmentRepository;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAssessmentService {

    private final ProductAssessmentRepository assessmentRepository;
    private final IRepositoryProduct productRepository;
    private final UserRepository userRepository;

    public ProductAssessment addAssessment(Long productId, Long userId, ProductAssessment assessment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return assessmentRepository.findByUser_IdAndProduct_Id(userId, productId)
                .map(existing -> {
                    existing.setStar(assessment.getStar());
                    existing.setComment(assessment.getComment());
                    return assessmentRepository.save(existing);
                })
                .orElseGet(() -> {
                    assessment.setProduct(product);
                    assessment.setUser(user);
                    return assessmentRepository.save(assessment);
                });
    }

    public List<ProductAssessment> getAssessmentsByProduct(Long productId) {
        return assessmentRepository.findByProduct_Id(productId);
    }

    public List<ProductAssessment> getAssessmentsByUser(Long userId) {
        return assessmentRepository.findByUser_Id(userId);
    }

    public double getAverageByProduct(Long productId) {
        Double avg = assessmentRepository.findAverageStarByProduct_Id(productId);
        return avg != null ? avg : 0.0;
    }

    public long getCountByProduct(Long productId) {
        return assessmentRepository.countByProduct_Id(productId);
    }

    public ProductAssessment updateAssessment(Long id, ProductAssessment updated) {
        ProductAssessment existing = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));
        existing.setStar(updated.getStar());
        existing.setComment(updated.getComment());
        return assessmentRepository.save(existing);
    }

    public void deleteAssessment(Long id) {
        if (!assessmentRepository.existsById(id)) {
            throw new RuntimeException("Assessment not found with id: " + id);
        }
        assessmentRepository.deleteById(id);
    }

    public ProductAssessment getById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + id));
    }
}
