package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.CertificateValidationDTO;

import java.util.List;

public interface ICertificateValidationService {
    List<CertificateValidationDTO> getAll();

    CertificateValidationDTO getById(Long id);

    CertificateValidationDTO create(CertificateValidationDTO dto);

    CertificateValidationDTO update(Long id, CertificateValidationDTO dto);

    void delete(Long id);
}
