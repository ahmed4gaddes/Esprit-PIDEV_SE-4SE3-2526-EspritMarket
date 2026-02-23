package tn.esprit.esprit_market.modules.marketing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.entity.Advertisement;
import tn.esprit.esprit_market.modules.marketing.services.IAdvertisementService;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdvertisementController {

    private final IAdvertisementService advertisementService;

    @PostMapping
    public Advertisement add(@RequestBody Advertisement ad) {
        return advertisementService.add(ad);
    }

    @PutMapping
    public Advertisement update(@RequestBody Advertisement ad) {
        return advertisementService.update(ad);
    }

    @GetMapping
    public List<Advertisement> getAll() {
        return advertisementService.getAll();
    }

    @GetMapping("/{id}")
    public Advertisement getById(@PathVariable Long id) {
        return advertisementService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        advertisementService.delete(id);
    }
}