package tn.esprit.esprit_market.modules.marketing.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.marketing.entity.MarketingCampaign;
import tn.esprit.esprit_market.modules.marketing.services.IMarketingCampaignService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")


@RequiredArgsConstructor
@CrossOrigin("*")
public class MarketingCampaignController {

    private final IMarketingCampaignService service;

    @PostMapping
    public MarketingCampaign add(@RequestBody MarketingCampaign c) {
        return service.add(c);
    }

    @PutMapping
    public MarketingCampaign update(@RequestBody MarketingCampaign c) {
        return service.update(c);
    }

    @GetMapping
    public List<MarketingCampaign> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarketingCampaign getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}