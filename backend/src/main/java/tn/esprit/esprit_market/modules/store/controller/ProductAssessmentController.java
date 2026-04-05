package tn.esprit.esprit_market.modules.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.entity.ProductAssessment;
import tn.esprit.esprit_market.modules.store.service.ProductAssessmentService;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductAssessmentController {

    private final ProductAssessmentService assessmentService;
    private final IUserService userService;

    // POST /api/assessments?productId=1
    // userId is resolved from authenticated user (secure)
    @PostMapping
    public ResponseEntity<ProductAssessment> addAssessment(
            @RequestParam Long productId,
            @RequestBody ProductAssessment assessment,
            Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("Only CUSTOMER can rate products.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentService.addAssessment(productId, currentUser.getId(), assessment));
    }

    // GET /api/assessments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductAssessment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getById(id));
    }

    // GET /api/assessments/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductAssessment>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByProduct(productId));
    }

    // GET /api/assessments/product/{productId}/average
    @GetMapping("/product/{productId}/average")
    public ResponseEntity<Map<String, Object>> getAverageByProduct(@PathVariable Long productId) {
        double average = assessmentService.getAverageByProduct(productId);
        long count = assessmentService.getCountByProduct(productId);
        return ResponseEntity.ok(Map.of(
                "average", average,
                "count", count
        ));
    }

    // GET /api/assessments/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductAssessment>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByUser(userId));
    }

    // PUT /api/assessments/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ProductAssessment> update(@PathVariable Long id,
                                                     @RequestBody ProductAssessment assessment) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, assessment));
    }

    // DELETE /api/assessments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.noContent().build();
    }
}
