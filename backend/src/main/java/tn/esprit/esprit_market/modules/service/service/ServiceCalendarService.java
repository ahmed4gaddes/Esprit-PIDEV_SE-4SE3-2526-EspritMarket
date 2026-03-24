package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.esprit_market.exceptions.ResourceNotFoundException;
import tn.esprit.esprit_market.modules.service.dto.ServiceCalendarDTO;
import tn.esprit.esprit_market.modules.service.entity.ServiceCalendar;
import tn.esprit.esprit_market.modules.service.mapper.ServiceModuleMapper;
import tn.esprit.esprit_market.modules.service.repository.ServiceCalendarRepository;
import tn.esprit.esprit_market.modules.service.repository.ServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceCalendarService implements IServiceCalendarService {
    private final ServiceCalendarRepository calendarRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceModuleMapper mapper;

    @Transactional(readOnly = true)
    public List<ServiceCalendarDTO> getAll() {
        return calendarRepository.findAll()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceCalendarDTO getById(Long id) {
        ServiceCalendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar entry not found with id: " + id));
        return mapper.toDto(calendar);
    }

    public ServiceCalendarDTO create(ServiceCalendarDTO dto) {
        ServiceCalendar calendar = new ServiceCalendar();
        mapper.toEntity(dto, calendar);

        if (dto.getServiceId() != null) {
            tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository
                    .findById(dto.getServiceId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Service not found with id: " + dto.getServiceId()));
            calendar.setService(service);
        }

        return mapper.toDto(calendarRepository.save(calendar));
    }

    public ServiceCalendarDTO update(Long id, ServiceCalendarDTO dto) {
        ServiceCalendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calendar entry not found with id: " + id));

        mapper.toEntity(dto, calendar);

        if (dto.getServiceId() != null
                && (calendar.getService() == null || !calendar.getService().getId().equals(dto.getServiceId()))) {
            tn.esprit.esprit_market.modules.service.entity.Service service = serviceRepository
                    .findById(dto.getServiceId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Service not found with id: " + dto.getServiceId()));
            calendar.setService(service);
        }

        return mapper.toDto(calendarRepository.save(calendar));
    }

    public void delete(Long id) {
        if (!calendarRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: Calendar entry not found with id: " + id);
        }
        calendarRepository.deleteById(id);
    }
}