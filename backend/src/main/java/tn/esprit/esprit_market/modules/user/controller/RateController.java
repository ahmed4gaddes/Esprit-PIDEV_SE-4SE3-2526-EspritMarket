package tn.esprit.esprit_market.modules.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.user.entity.Rate;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.service.IUserService;
import tn.esprit.esprit_market.modules.user.service.RateService;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RateController {

    private final RateService rateService;
    private final IUserService userService;

    // POST /api/rates?ratedUserId=2
    // raterId is resolved from authenticated user (secure)
    @PostMapping
    public ResponseEntity<Rate> addRate(
            @RequestParam Long ratedUserId,
            @RequestBody Rate rate,
            Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        if (currentUser.getRole() != Role.CUSTOMER) {
            throw new AccessDeniedException("Only CUSTOMER can rate users.");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rateService.addRate(currentUser.getId(), ratedUserId, rate));
    }

    // GET /api/rates/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Rate> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rateService.getById(id));
    }

    // GET /api/rates/user/{ratedUserId}
    @GetMapping("/user/{ratedUserId}")
    public ResponseEntity<List<Rate>> getRatesForUser(@PathVariable Long ratedUserId) {
        return ResponseEntity.ok(rateService.getRatesForUser(ratedUserId));
    }

    // GET /api/rates/user/{ratedUserId}/average
    @GetMapping("/user/{ratedUserId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long ratedUserId) {
        return ResponseEntity.ok(rateService.getAverageRating(ratedUserId));
    }

    // GET /api/rates/rater/{raterId}
    @GetMapping("/rater/{raterId}")
    public ResponseEntity<List<Rate>> getRatesByRater(@PathVariable Long raterId) {
        return ResponseEntity.ok(rateService.getRatesByRater(raterId));
    }

    // PUT /api/rates/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Rate> update(@PathVariable Long id, @RequestBody Rate rate) {
        return ResponseEntity.ok(rateService.updateRate(id, rate));
    }

    // DELETE /api/rates/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rateService.deleteRate(id);
        return ResponseEntity.noContent().build();
    }
}
