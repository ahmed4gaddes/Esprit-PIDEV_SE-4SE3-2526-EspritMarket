package tn.esprit.esprit_market.modules.marketing.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.entity.Sponsorship;
import tn.esprit.esprit_market.modules.marketing.services.ISponsorshipService;
import java.util.List;

@RestController
@RequestMapping("/api/sponsorships")
@RequiredArgsConstructor
public class SponsorshipController {

    private final ISponsorshipService service;

    @PostMapping
    public Sponsorship add(@RequestBody Sponsorship s) {
        return service.add(s);
    }

    @PutMapping
    public Sponsorship update(@RequestBody Sponsorship s) {
        return service.update(s);
    }

    @GetMapping
    public List<Sponsorship> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Sponsorship getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}