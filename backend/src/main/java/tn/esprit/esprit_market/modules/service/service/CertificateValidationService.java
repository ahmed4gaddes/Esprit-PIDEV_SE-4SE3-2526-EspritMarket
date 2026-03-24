package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.CertificateValidationDTO;
import tn.esprit.esprit_market.modules.service.entity.Certificate;
import tn.esprit.esprit_market.modules.service.entity.CertificateValidation;
import tn.esprit.esprit_market.modules.service.enums.ValidationStatus;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.CertificateRepository;
import tn.esprit.esprit_market.modules.service.repository.CertificateValidationRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CertificateValidationService implements ICertificateValidationService {
    private final CertificateValidationRepository validationRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<CertificateValidationDTO> getAll() {
        return validationRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CertificateValidationDTO getById(Long id) {
        CertificateValidation validation = validationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Validation record not found with id: " + id));
        return mapper.toDto(validation);
    }

    public CertificateValidationDTO create(CertificateValidationDTO dto) {
        CertificateValidation validation = new CertificateValidation();
        mapper.toEntity(dto, validation);

        validation.setValidationDate(new Date());

        // Ensure default status is PENDING if null
        if (dto.getStatus() == null) {
            validation.setStatus(ValidationStatus.PENDING);
        } else {
            validation.setStatus(dto.getStatus());
        }

        if (dto.getCertificateId() != null) {
            Certificate certificate = certificateRepository.findById(dto.getCertificateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Certificate not found with id: " + dto.getCertificateId()));
            validation.setCertificate(certificate);

            // Workflow logic
            if (validation.getStatus() == ValidationStatus.APPROVED) {
                certificate.setStatus(ValidationStatus.APPROVED);
                certificate.setValidationDate(new Date());
                certificateRepository.save(certificate);
            } else if (validation.getStatus() == ValidationStatus.REJECTED) {
                certificate.setStatus(ValidationStatus.REJECTED);
                certificate.setAdminComment(validation.getComment());
                certificateRepository.save(certificate);
            }
        }

        if (dto.getValidatorId() != null) {
            User validator = userRepository.findById(dto.getValidatorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Validator User not found with id: " + dto.getValidatorId()));
            validation.setValidator(validator);
        }

        return mapper.toDto(validationRepository.save(validation));
    }

    public CertificateValidationDTO update(Long id, CertificateValidationDTO dto) {
        CertificateValidation validation = validationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Validation record not found with id: " + id));

        mapper.toEntity(dto, validation);

        if (dto.getStatus() != null) {
            validation.setStatus(dto.getStatus());

            // Re-apply workflow logic if status changed
            if (validation.getCertificate() != null) {
                Certificate certificate = validation.getCertificate();
                if (validation.getStatus() == ValidationStatus.APPROVED) {
                    certificate.setStatus(ValidationStatus.APPROVED);
                    certificate.setValidationDate(new Date());
                } else if (validation.getStatus() == ValidationStatus.REJECTED) {
                    certificate.setStatus(ValidationStatus.REJECTED);
                    certificate.setAdminComment(validation.getComment());
                }
                certificateRepository.save(certificate);
            }
        }

        if (dto.getValidatorId() != null && (validation.getValidator() == null
                || !validation.getValidator().getId().equals(dto.getValidatorId()))) {
            User validator = userRepository.findById(dto.getValidatorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Validator User not found with id: " + dto.getValidatorId()));
            validation.setValidator(validator);
        }

        return mapper.toDto(validationRepository.save(validation));
    }

    public void delete(Long id) {
        if (!validationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Validation record not found with id: " + id);
        }
        validationRepository.deleteById(id);
    }
}