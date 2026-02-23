package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.service.CertificateService;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificateController {
    private final CertificateService certificateService;

    @GetMapping
    public List<Certificate> getAll() {
        return certificateService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getById(@PathVariable Long id) {
        return ResponseEntity.ok(certificateService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Certificate> create(@RequestBody Certificate certificate) {
        return new ResponseEntity<>(certificateService.create(certificate), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certificate> update(@PathVariable Long id, @RequestBody Certificate certificate) {
        return ResponseEntity.ok(certificateService.update(id, certificate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        certificateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}