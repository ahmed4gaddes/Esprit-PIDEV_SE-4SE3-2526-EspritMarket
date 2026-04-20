package tn.esprit.esprit_market.modules.store.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class MLService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_API = "http://localhost:5000/predict";

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> search(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("query", query);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<List> response = restTemplate.exchange(
                PYTHON_API,
                HttpMethod.POST,
                entity,
                List.class
        );

        return response.getBody();
    }
}