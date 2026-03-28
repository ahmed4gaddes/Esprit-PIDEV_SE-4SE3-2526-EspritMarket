package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.InternshipDTO;
import tn.esprit.esprit_market.modules.service.entity.Internship;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.InternshipApplicationRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.shared.service.NotificationService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InternshipService implements IInternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<InternshipDTO> getAll() {
        return internshipRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InternshipDTO getById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found with id: " + id));
        return mapper.toDto(internship);
    }

    public InternshipDTO create(InternshipDTO dto) {
        Internship internship = new Internship();
        mapper.toEntity(dto, internship);

        internship.setType(ServiceType.INTERNSHIP);

        if (dto.getCreatorId() != null) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            internship.setCreator(creator);
        }

        Internship saved = internshipRepository.save(internship);

        // Notify customers that a new internship offer is available.
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        for (User customer : customers) {
            notificationService.notifyUser(
                    customer.getId(),
                    "New internship offer",
                    "A new internship offer \"" + saved.getTitle() + "\" has been posted. Check Stages and apply.");
        }

        return mapper.toDto(saved);
    }

    public InternshipDTO update(Long id, InternshipDTO dto) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update: Internship not found with id: " + id));

        mapper.toEntity(dto, internship);

        if (dto.getCreatorId() != null
                && (internship.getCreator() == null || !internship.getCreator().getId().equals(dto.getCreatorId()))) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            internship.setCreator(creator);
        }

        return mapper.toDto(internshipRepository.save(internship));
    }

    public void delete(Long id) {
        if (!internshipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Internship not found with id: " + id);
        }
        internshipApplicationRepository.deleteByInternship_Id(id);
        internshipRepository.deleteById(id);
    }
}