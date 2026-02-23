package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.Gamification;
import tn.esprit.esprit_market.modules.service.service.GamificationService;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GamificationController {
    private final GamificationService gamificationService;

    @GetMapping
    public List<Gamification> getAll() {
        return gamificationService.getAll();
    }

    @GetMapping("/user/{id}") // Often searched by User ID
    public ResponseEntity<Gamification> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gamificationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gamification> update(@PathVariable Long id, @RequestBody Gamification gamification) {
        return ResponseEntity.ok(gamificationService.update(id, gamification));
    }
}