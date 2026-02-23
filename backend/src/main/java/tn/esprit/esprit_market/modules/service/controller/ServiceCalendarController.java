package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.ServiceCalendar;
import tn.esprit.esprit_market.modules.service.service.ServiceCalendarService;

import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ServiceCalendarController {
    private final ServiceCalendarService calendarService;

    @GetMapping
    public List<ServiceCalendar> getAll() {
        return calendarService.getAll();
    }

    @PostMapping
    public ResponseEntity<ServiceCalendar> create(@RequestBody ServiceCalendar calendar) {
        return new ResponseEntity<>(calendarService.create(calendar), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceCalendar> update(@PathVariable Long id, @RequestBody ServiceCalendar calendar) {
        return ResponseEntity.ok(calendarService.update(id, calendar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        calendarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}