package tn.esprit.esprit_market.modules.store.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.esprit_market.modules.store.dto.ProductRequest;

@Service
public class AiService {

    private final RestTemplate restTemplate;

    public AiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getRecommendations(ProductRequest req) {
        String url = "http://localhost:5000/recommend";
        return restTemplate.postForObject(url, req, String.class);
    }
}