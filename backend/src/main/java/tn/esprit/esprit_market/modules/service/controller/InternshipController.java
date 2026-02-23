package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.service.InternshipService;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InternshipController {
    private final InternshipService internshipService;

    @GetMapping
    public List<Internship> getAll() {
        return internshipService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Internship> getById(@PathVariable Long id) {
        return ResponseEntity.ok(internshipService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Internship> create(@RequestBody Internship internship) {
        return new ResponseEntity<>(internshipService.create(internship), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Internship> update(@PathVariable Long id, @RequestBody Internship internship) {
        return ResponseEntity.ok(internshipService.update(id, internship));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        internshipService.delete(id);
        return ResponseEntity.noContent().build();
    }
}