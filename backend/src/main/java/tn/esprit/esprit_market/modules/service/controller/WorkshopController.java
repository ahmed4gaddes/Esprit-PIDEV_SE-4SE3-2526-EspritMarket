package tn.esprit.esprit_market.modules.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.service.entity.Workshop;
import tn.esprit.esprit_market.modules.service.service.WorkshopService;

import java.util.List;

@RestController
@RequestMapping("/api/workshops")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkshopController {
    private final WorkshopService workshopService;

    @GetMapping
    public List<Workshop> getAll() {
        return workshopService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workshop> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workshopService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Workshop> create(@RequestBody Workshop workshop) {
        return new ResponseEntity<>(workshopService.create(workshop), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workshop> update(@PathVariable Long id, @RequestBody Workshop workshop) {
        return ResponseEntity.ok(workshopService.update(id, workshop));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workshopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}