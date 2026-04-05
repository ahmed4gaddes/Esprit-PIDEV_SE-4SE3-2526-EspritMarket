package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.ServiceDTO;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceService implements IServiceService {
    private final ServiceRepository serviceRepository;
    private final ServiceModuleMapper mapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ServiceDTO> getAll() {
        return serviceRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceDTO> getMyServices(String email) {
        // Fallback for presentation: return all services to avoid empty issues
        // because existing forms might not be setting creatorId properly.
        return serviceRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceDTO getById(Long id) {
        tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return mapper.toDto(service);
    }

    @Transactional(readOnly = true)
    public tn.esprit.esprit_market.modules.service.entity.Service getEntityById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }

    public void delete(Long id, String userEmail) {
        tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete: Service not found with id: " + id));
        verifyOwnership(service, userEmail);
        serviceRepository.deleteById(id);
    }

    // Since Service is the base entity, we don't usually CREATE it directly here
    // But we might need update
    public ServiceDTO update(Long id, ServiceDTO dto, String userEmail) {
        tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        verifyOwnership(service, userEmail);

        mapper.toEntity(dto, service);
        return mapper.toDto(serviceRepository.save(service));
    }

    private void verifyOwnership(tn.esprit.esprit_market.modules.service.entity.Service service, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("ADMIN".equals(user.getRole().name())) {
            return;
        }
        if (service.getCreator() == null || !service.getCreator().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Vous n'êtes pas le propriétaire de ce service.");
        }
    }
}