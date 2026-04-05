package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.SupportingDocumentDTO;

import java.util.List;

public interface ISupportingDocumentService {
    List<SupportingDocumentDTO> getAll();

    SupportingDocumentDTO getById(Long id);

    SupportingDocumentDTO create(SupportingDocumentDTO dto);

    SupportingDocumentDTO update(Long id, SupportingDocumentDTO dto);

    void delete(Long id);
}
