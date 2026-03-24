package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.SupportingDocumentDTO;
import tn.esprit.esprit_market.modules.service.service.ISupportingDocumentService;

import java.util.List;

@RestController
@RequestMapping("/api/supporting-documents")
@RequiredArgsConstructor
public class SupportingDocumentController {

    private final ISupportingDocumentService documentService;

    @GetMapping
    public ResponseEntity<List<SupportingDocumentDTO>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportingDocumentDTO> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<SupportingDocumentDTO> createDocument(@Valid @RequestBody SupportingDocumentDTO documentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.create(documentDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<SupportingDocumentDTO> updateDocument(@PathVariable Long id,
            @Valid @RequestBody SupportingDocumentDTO documentDTO) {
        return ResponseEntity.ok(documentService.update(id, documentDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}