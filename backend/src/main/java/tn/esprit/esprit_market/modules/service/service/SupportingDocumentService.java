package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.SupportingDocument;
import tn.esprit.esprit_market.modules.service.repository.SupportingDocumentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportingDocumentService {
    private final SupportingDocumentRepository documentRepository;

    public List<SupportingDocument> getAll() {
        return documentRepository.findAll();
    }

    public SupportingDocument getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public SupportingDocument create(SupportingDocument document) {
        return documentRepository.save(document);
    }

    public SupportingDocument update(Long id, SupportingDocument document) {
        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Document not found");
        }
        document.setId(id);
        return documentRepository.save(document);
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }
}