package tn.esprit.esprit_market.modules.marketing.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.entity.SponsorshipRequest;
import tn.esprit.esprit_market.modules.marketing.services.ISponsorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/sponsorship-requests")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SponsorshipRequestController {

    private final ISponsorshipRequestService service;

    @PostMapping
    public SponsorshipRequest add(@RequestBody SponsorshipRequest r) {
        return service.add(r);
    }

    @PutMapping
    public SponsorshipRequest update(@RequestBody SponsorshipRequest r) {
        return service.update(r);
    }

    @GetMapping
    public List<SponsorshipRequest> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SponsorshipRequest getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {   // @PathVariable ajouté
        service.delete(id);
    }
}