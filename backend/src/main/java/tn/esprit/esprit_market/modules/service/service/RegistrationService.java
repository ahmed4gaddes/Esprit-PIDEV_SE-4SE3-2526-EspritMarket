package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.RegistrationDTO;
import tn.esprit.esprit_market.modules.service.entity.Registration;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.enums.RegistrationStatus;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.RegistrationRepository;
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
public class RegistrationService implements IRegistrationService {
    private final RegistrationRepository registrationRepository;
    private final WorkshopRepository workshopRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<RegistrationDTO> getAll() {
        return registrationRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RegistrationDTO getById(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));
        return mapper.toDto(registration);
    }

    @Transactional(readOnly = true)
    public List<RegistrationDTO> getByWorkshopId(Long workshopId) {
        return registrationRepository.findByWorkshopId(workshopId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationDTO> getByUserId(Long userId) {
        return registrationRepository.findByUserId(userId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = org.springframework.transaction.annotation.Isolation.SERIALIZABLE)
    public RegistrationDTO create(RegistrationDTO dto) {
        Registration registration = new Registration();
        mapper.toEntity(dto, registration);

        registration.setRegistrationDate(new Date());
        registration.setStatus(RegistrationStatus.REGISTERED);

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));
            registration.setUser(user);
        }

        if (dto.getWorkshopId() != null) {
            Workshop workshop = workshopRepository.findById(dto.getWorkshopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Workshop not found: " + dto.getWorkshopId()));

            // Capacity check
            if (workshop.getEnrolledCount() >= workshop.getCapacity()) {
                throw new IllegalStateException("Workshop is already full.");
            }

            workshop.setEnrolledCount(workshop.getEnrolledCount() + 1);
            workshopRepository.save(workshop);

            registration.setWorkshop(workshop);
        }

        return mapper.toDto(registrationRepository.save(registration));
    }

    public RegistrationDTO update(Long id, RegistrationDTO dto) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + id));

        // Detect cancelled transition
        RegistrationStatus oldStatus = registration.getStatus();
        mapper.toEntity(dto, registration);

        if (dto.getStatus() != null) {
            registration.setStatus(dto.getStatus());

            // Handle cancellation - free up capacity
            if (oldStatus != RegistrationStatus.CANCELLED && dto.getStatus() == RegistrationStatus.CANCELLED) {
                if (registration.getWorkshop() != null) {
                    Workshop workshop = registration.getWorkshop();
                    if (workshop.getEnrolledCount() > 0) {
                        workshop.setEnrolledCount(workshop.getEnrolledCount() - 1);
                        workshopRepository.save(workshop);
                    }
                }
            }
        }

        return mapper.toDto(registrationRepository.save(registration));
    }

    public void delete(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cannot delete: Registration not found with id: " + id));

        // Open up capacity
        if (registration.getStatus() != RegistrationStatus.CANCELLED && registration.getWorkshop() != null) {
            Workshop workshop = registration.getWorkshop();
            if (workshop.getEnrolledCount() > 0) {
                workshop.setEnrolledCount(workshop.getEnrolledCount() - 1);
                workshopRepository.save(workshop);
            }
        }

        registrationRepository.deleteById(id);
    }
}