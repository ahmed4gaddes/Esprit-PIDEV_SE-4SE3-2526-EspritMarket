package tn.esprit.esprit_market.modules.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.service.entity.ServiceCalendar;
import tn.esprit.esprit_market.modules.service.repository.ServiceCalendarRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceCalendarService {
    private final ServiceCalendarRepository calendarRepository;

    public List<ServiceCalendar> getAll() {
        return calendarRepository.findAll();
    }

    public ServiceCalendar getById(Long id) {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calendar entry not found"));
    }

    public ServiceCalendar create(ServiceCalendar calendar) {
        return calendarRepository.save(calendar);
    }

    public ServiceCalendar update(Long id, ServiceCalendar calendar) {
        if (!calendarRepository.existsById(id)) {
            throw new RuntimeException("Calendar entry not found");
        }
        calendar.setId(id);
        return calendarRepository.save(calendar);
    }

    public void delete(Long id) {
        calendarRepository.deleteById(id);
    }
}