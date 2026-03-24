package tn.esprit.esprit_market.modules.service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.dto.ServiceCalendarDTO;
import tn.esprit.esprit_market.modules.service.service.IServiceCalendarService;

import java.util.List;

@RestController
@RequestMapping("/api/service-calendars")
@RequiredArgsConstructor
public class ServiceCalendarController {

    private final IServiceCalendarService calendarService;

    @GetMapping
    public ResponseEntity<List<ServiceCalendarDTO>> getAllCalendars() {
        return ResponseEntity.ok(calendarService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceCalendarDTO> getCalendarById(@PathVariable Long id) {
        return ResponseEntity.ok(calendarService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<ServiceCalendarDTO> createCalendar(@Valid @RequestBody ServiceCalendarDTO calendarDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarService.create(calendarDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCalendarDTO> updateCalendar(@PathVariable Long id,
            @Valid @RequestBody ServiceCalendarDTO calendarDTO) {
        return ResponseEntity.ok(calendarService.update(id, calendarDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_EXPERT', 'ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable Long id) {
        calendarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}