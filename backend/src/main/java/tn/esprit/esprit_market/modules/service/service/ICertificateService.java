package tn.esprit.esprit_market.modules.service.service;

import tn.esprit.esprit_market.modules.service.dto.CertificateDTO;
import tn.esprit.esprit_market.modules.service.dto.CertificateEligibilityDTO;

import java.util.List;

public interface ICertificateService {
    List<CertificateDTO> getAll();

    CertificateDTO getById(Long id);

    CertificateDTO create(CertificateDTO dto);

    CertificateDTO update(Long id, CertificateDTO dto);

    void delete(Long id);

    CertificateEligibilityDTO getEligibility(Long certificateId, Long userId);
}
