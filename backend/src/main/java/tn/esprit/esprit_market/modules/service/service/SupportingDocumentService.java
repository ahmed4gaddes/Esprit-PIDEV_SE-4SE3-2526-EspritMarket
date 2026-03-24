package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.SupportingDocumentDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.SupportingDocument;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.service.repository.SupportingDocumentRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SupportingDocumentService implements ISupportingDocumentService {
    private final SupportingDocumentRepository documentRepository;
    private final CertificateRepository certificateRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<SupportingDocumentDTO> getAll() {
        return documentRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupportingDocumentDTO getById(Long id) {
        SupportingDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return mapper.toDto(document);
    }

    public SupportingDocumentDTO create(SupportingDocumentDTO dto) {
        SupportingDocument document = new SupportingDocument();
        mapper.toEntity(dto, document);

        if (document.getUploadDate() == null) {
            document.setUploadDate(new Date());
        }

        if (dto.getCertificateId() != null) {
            Certificate certificate = certificateRepository.findById(dto.getCertificateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Certificate not found with id: " + dto.getCertificateId()));
            document.setCertificate(certificate);
        }

        return mapper.toDto(documentRepository.save(document));
    }

    public SupportingDocumentDTO update(Long id, SupportingDocumentDTO dto) {
        SupportingDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        mapper.toEntity(dto, document);

        if (dto.getCertificateId() != null && (document.getCertificate() == null
                || !document.getCertificate().getId().equals(dto.getCertificateId()))) {
            Certificate certificate = certificateRepository.findById(dto.getCertificateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Certificate not found with id: " + dto.getCertificateId()));
            document.setCertificate(certificate);
        }

        return mapper.toDto(documentRepository.save(document));
    }

    public void delete(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Document not found with id: " + id);
        }
        documentRepository.deleteById(id);
    }
}