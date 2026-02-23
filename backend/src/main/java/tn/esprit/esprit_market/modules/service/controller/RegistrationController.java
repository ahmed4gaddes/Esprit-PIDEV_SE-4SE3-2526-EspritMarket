package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.service.RegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping
    public List<Registration> getAll() {
        return registrationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registration> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Registration> create(@RequestBody Registration registration) {
        return new ResponseEntity<>(registrationService.create(registration), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Registration> update(@PathVariable Long id, @RequestBody Registration registration) {
        return ResponseEntity.ok(registrationService.update(id, registration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}