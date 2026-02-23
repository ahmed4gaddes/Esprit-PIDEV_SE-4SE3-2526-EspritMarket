package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.CertificateValidation;
import tn.esprit.esprit_market.modules.service.service.CertificateValidationService;

import java.util.List;

@RestController
@RequestMapping("/api/certificate-validations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificateValidationController {
    private final CertificateValidationService validationService;

    @GetMapping
    public List<CertificateValidation> getAll() {
        return validationService.getAll();
    }

    @PostMapping
    public ResponseEntity<CertificateValidation> create(@RequestBody CertificateValidation validation) {
        return new ResponseEntity<>(validationService.create(validation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateValidation> update(@PathVariable Long id, @RequestBody CertificateValidation validation) {
        return ResponseEntity.ok(validationService.update(id, validation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        validationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}