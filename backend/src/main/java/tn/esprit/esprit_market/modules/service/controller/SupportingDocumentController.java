package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.SupportingDocument;
import tn.esprit.esprit_market.modules.service.service.SupportingDocumentService;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupportingDocumentController {
    private final SupportingDocumentService documentService;

    @GetMapping
    public List<SupportingDocument> getAll() {
        return documentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportingDocument> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SupportingDocument> create(@RequestBody SupportingDocument document) {
        return new ResponseEntity<>(documentService.create(document), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportingDocument> update(@PathVariable Long id, @RequestBody SupportingDocument document) {
        return ResponseEntity.ok(documentService.update(id, document));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}