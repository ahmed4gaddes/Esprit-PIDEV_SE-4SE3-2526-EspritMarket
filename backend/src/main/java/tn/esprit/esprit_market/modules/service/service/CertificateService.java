package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CertificateDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService implements ICertificateService {
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<CertificateDTO> getAll() {
        return certificateRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CertificateDTO getById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id));
        return mapper.toDto(certificate);
    }

    public CertificateDTO create(CertificateDTO dto) {
        Certificate certificate = new Certificate();
        mapper.toEntity(dto, certificate);

        certificate.setType(ServiceType.CERTIFICATE);

        if (dto.getCreatorId() != null) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            certificate.setCreator(creator);
        }

        return mapper.toDto(certificateRepository.save(certificate));
    }

    public CertificateDTO update(Long id, CertificateDTO dto) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + id));

        mapper.toEntity(dto, certificate);

        if (dto.getCreatorId() != null
                && (certificate.getCreator() == null || !certificate.getCreator().getId().equals(dto.getCreatorId()))) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            certificate.setCreator(creator);
        }

        return mapper.toDto(certificateRepository.save(certificate));
    }

    public void delete(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Certificate not found with id: " + id);
        }
        certificateRepository.deleteById(id);
    }
}