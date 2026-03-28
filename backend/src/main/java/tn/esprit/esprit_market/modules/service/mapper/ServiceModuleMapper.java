package tn.esprit.esprit_market.modules.service.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.esprit_market.modules.service.dto.*;
import tn.esprit.esprit_market.modules.service.entity.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceModuleMapper {

    // --- Service ---
    public ServiceDTO toDto(Service entity) {
        if (entity == null) return null;
        ServiceDTO dto = new ServiceDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getCreator() != null) {
            dto.setCreatorId(entity.getCreator().getId());
        }
        return dto;
    }

    public void toEntity(ServiceDTO dto, Service entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setActive(dto.isActive());
        entity.setImageUrl(dto.getImageUrl());
        // Note: 'creator' and 'type' usually handled by Service layer
    }

    // --- Workshop ---
    public WorkshopDTO toDto(Workshop entity) {
        if (entity == null) return null;
        WorkshopDTO dto = new WorkshopDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getCreator() != null) {
            dto.setCreatorId(entity.getCreator().getId());
        }
        dto.setDurationHours(entity.getDurationHours());
        dto.setCapacity(entity.getCapacity());
        dto.setEnrolledCount(entity.getEnrolledCount());
        dto.setPrerequisites(entity.getPrerequisites());
        dto.setProvidedMaterial(entity.getProvidedMaterial());
        dto.setDifficultyLevel(entity.getDifficultyLevel());
        return dto;
    }

    public void toEntity(WorkshopDTO dto, Workshop entity) {
        if (dto == null || entity == null) return;
        toEntity((ServiceDTO) null, entity); // Map base service properties manually
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setActive(dto.isActive());
        entity.setImageUrl(dto.getImageUrl());
        entity.setDurationHours(dto.getDurationHours());
        entity.setCapacity(dto.getCapacity());
        entity.setPrerequisites(dto.getPrerequisites());
        entity.setProvidedMaterial(dto.getProvidedMaterial());
        entity.setDifficultyLevel(dto.getDifficultyLevel());
    }

    // --- Certificate ---
    public CertificateDTO toDto(Certificate entity) {
        if (entity == null) return null;
        CertificateDTO dto = new CertificateDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getCreator() != null) {
            dto.setCreatorId(entity.getCreator().getId());
        }
        dto.setOrganization(entity.getOrganization());
        dto.setValidUntil(entity.getValidUntil());
        dto.setLevel(entity.getLevel());
        dto.setStatus(entity.getStatus());
        dto.setDocumentUrl(entity.getDocumentUrl());
        dto.setValidationDate(entity.getValidationDate());
        dto.setAdminComment(entity.getAdminComment());
        if (entity.getRequiredCourses() != null && !entity.getRequiredCourses().isEmpty()) {
            List<CourseDTO> courses = entity.getRequiredCourses().stream()
                    .sorted(Comparator.comparing(Course::getCourseOrder).thenComparing(Course::getId))
                    .map(this::toDto)
                    .collect(Collectors.toList());
            dto.setCourses(courses);
            dto.setCourseIds(courses.stream().map(CourseDTO::getId).collect(Collectors.toList()));
        }
        return dto;
    }

    public void toEntity(CertificateDTO dto, Certificate entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setActive(dto.isActive());
        entity.setImageUrl(dto.getImageUrl());
        entity.setOrganization(dto.getOrganization());
        entity.setValidUntil(dto.getValidUntil());
        entity.setLevel(dto.getLevel());
        entity.setDocumentUrl(dto.getDocumentUrl());
        // requiredCourses est géré dans CertificateService (chargement par IDs)
    }

    // --- Internship ---
    public InternshipDTO toDto(Internship entity) {
        if (entity == null) return null;
        InternshipDTO dto = new InternshipDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setType(entity.getType());
        dto.setActive(entity.isActive());
        dto.setCreatedAt(entity.getCreatedAt());
        if (entity.getCreator() != null) {
            dto.setCreatorId(entity.getCreator().getId());
        }
        dto.setCompany(entity.getCompany());
        dto.setDurationMonths(entity.getDurationMonths());
        dto.setCompanyAddress(entity.getCompanyAddress());
        dto.setCompanyTutorName(entity.getCompanyTutorName());
        dto.setCompanyTutorEmail(entity.getCompanyTutorEmail());
        dto.setAcademicTutorName(entity.getAcademicTutorName());
        dto.setAgreementSigned(entity.isAgreementSigned());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        return dto;
    }

    public void toEntity(InternshipDTO dto, Internship entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setActive(dto.isActive());
        entity.setCompany(dto.getCompany());
        entity.setDurationMonths(dto.getDurationMonths());
        entity.setCompanyAddress(dto.getCompanyAddress());
        entity.setCompanyTutorName(dto.getCompanyTutorName());
        entity.setCompanyTutorEmail(dto.getCompanyTutorEmail());
        entity.setAcademicTutorName(dto.getAcademicTutorName());
        entity.setAgreementSigned(dto.isAgreementSigned());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }

    // --- Course ---
    public CourseDTO toDto(Course entity) {
        if (entity == null) return null;
        CourseDTO dto = new CourseDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDurationHours(entity.getDurationHours());
        dto.setMandatory(entity.isMandatory());
        dto.setDescription(entity.getDescription());
        dto.setObjectives(entity.getObjectives());
        dto.setCourseOrder(entity.getCourseOrder());
        if (entity.getWorkshop() != null) {
            dto.setWorkshopId(entity.getWorkshop().getId());
        }
        // certificateId est renseigné dans CourseService (relation via Certificate.requiredCourses)
        return dto;
    }

    public void toEntity(CourseDTO dto, Course entity) {
        if (dto == null || entity == null) return;
        entity.setTitle(dto.getTitle());
        entity.setDurationHours(dto.getDurationHours());
        entity.setMandatory(dto.isMandatory());
        entity.setDescription(dto.getDescription());
        entity.setObjectives(dto.getObjectives());
        entity.setCourseOrder(dto.getCourseOrder());
    }

    // --- Registration ---
    public RegistrationDTO toDto(Registration entity) {
        if (entity == null) return null;
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(entity.getId());
        dto.setRegistrationDate(entity.getRegistrationDate());
        dto.setStatus(entity.getStatus());
        dto.setAttendanceConfirmed(entity.isAttendanceConfirmed());
        dto.setEvaluationScore(entity.getEvaluationScore());
        dto.setComment(entity.getComment());
        if (entity.getWorkshop() != null) {
            dto.setWorkshopId(entity.getWorkshop().getId());
        }
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        return dto;
    }

    public void toEntity(RegistrationDTO dto, Registration entity) {
        if (dto == null || entity == null) return;
        entity.setAttendanceConfirmed(dto.isAttendanceConfirmed());
        entity.setEvaluationScore(dto.getEvaluationScore());
        entity.setComment(dto.getComment());
    }

    // --- ServiceCalendar ---
    public ServiceCalendarDTO toDto(ServiceCalendar entity) {
        if (entity == null) return null;
        ServiceCalendarDTO dto = new ServiceCalendarDTO();
        dto.setId(entity.getId());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCapacity(entity.getCapacity());
        dto.setAvailable(entity.isAvailable());
        if (entity.getService() != null) {
            dto.setServiceId(entity.getService().getId());
        }
        return dto;
    }

    public void toEntity(ServiceCalendarDTO dto, ServiceCalendar entity) {
        if (dto == null || entity == null) return;
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setCapacity(dto.getCapacity());
        entity.setAvailable(dto.isAvailable());
    }

    // --- SupportingDocument ---
    public SupportingDocumentDTO toDto(SupportingDocument entity) {
        if (entity == null) return null;
        SupportingDocumentDTO dto = new SupportingDocumentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUrl(entity.getUrl());
        dto.setType(entity.getType());
        dto.setUploadDate(entity.getUploadDate());
        dto.setSize(entity.getSize());
        if (entity.getCertificate() != null) {
            dto.setCertificateId(entity.getCertificate().getId());
        }
        return dto;
    }

    public void toEntity(SupportingDocumentDTO dto, SupportingDocument entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        entity.setType(dto.getType());
        entity.setSize(dto.getSize());
    }

    // --- CertificateValidation ---
    public CertificateValidationDTO toDto(CertificateValidation entity) {
        if (entity == null) return null;
        CertificateValidationDTO dto = new CertificateValidationDTO();
        dto.setId(entity.getId());
        dto.setValidationDate(entity.getValidationDate());
        dto.setStatus(entity.getStatus());
        dto.setComment(entity.getComment());
        if (entity.getCertificate() != null) {
            dto.setCertificateId(entity.getCertificate().getId());
        }
        if (entity.getValidator() != null) {
            dto.setValidatorId(entity.getValidator().getId());
        }
        return dto;
    }

    public void toEntity(CertificateValidationDTO dto, CertificateValidation entity) {
        if (dto == null || entity == null) return;
        entity.setComment(dto.getComment());
    }

    // --- WorkshopEvaluation ---
    public WorkshopEvaluationDTO toDto(WorkshopEvaluation entity) {
        if (entity == null) return null;
        WorkshopEvaluationDTO dto = new WorkshopEvaluationDTO();
        dto.setId(entity.getId());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setEvaluationDate(entity.getEvaluationDate());
        if (entity.getRegistration() != null) {
            dto.setRegistrationId(entity.getRegistration().getId());
        }
        return dto;
    }

    public void toEntity(WorkshopEvaluationDTO dto, WorkshopEvaluation entity) {
        if (dto == null || entity == null) return;
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
    }

    // --- Gamification ---
    public GamificationDTO toDto(Gamification entity) {
        if (entity == null) return null;
        GamificationDTO dto = new GamificationDTO();
        dto.setId(entity.getId());
        dto.setPoints(entity.getPoints());
        dto.setLevel(entity.getLevel());
        dto.setBadge(entity.getBadge());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        return dto;
    }

    public void toEntity(GamificationDTO dto, Gamification entity) {
        if (dto == null || entity == null) return;
        entity.setPoints(dto.getPoints());
        entity.setLevel(dto.getLevel());
        entity.setBadge(dto.getBadge());
    }
}
