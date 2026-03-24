package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.WorkshopDTO;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.enums.ServiceType;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.WorkshopRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WorkshopService implements IWorkshopService {
    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<WorkshopDTO> getAll() {
        return workshopRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkshopDTO getById(Long id) {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with id: " + id));
        return mapper.toDto(workshop);
    }

    public WorkshopDTO create(WorkshopDTO dto) {
        Workshop workshop = new Workshop();
        mapper.toEntity(dto, workshop);

        workshop.setType(ServiceType.WORKSHOP);
        // Start enrolled count at 0
        workshop.setEnrolledCount(0);

        if (dto.getCreatorId() != null) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            workshop.setCreator(creator);
        }

        return mapper.toDto(workshopRepository.save(workshop));
    }

    public WorkshopDTO update(Long id, WorkshopDTO dto) {
        Workshop workshop = workshopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workshop not found with id: " + id));

        mapper.toEntity(dto, workshop);

        if (dto.getCreatorId() != null
                && (workshop.getCreator() == null || !workshop.getCreator().getId().equals(dto.getCreatorId()))) {
            User creator = userRepository.findById(dto.getCreatorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Creator not found with id: " + dto.getCreatorId()));
            workshop.setCreator(creator);
        }

        return mapper.toDto(workshopRepository.save(workshop));
    }

    public void delete(Long id) {
        if (!workshopRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Workshop not found with id: " + id);
        }
        workshopRepository.deleteById(id);
    }
}