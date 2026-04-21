//package tn.esprit.esprit_market.modules.store.service;
//
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class MLService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final String PYTHON_API = "http://localhost:5000";
//
//    // ── Recherche sémantique ─────────────────────────────────────
//    @SuppressWarnings("unchecked")
//    public List<Map<String, Object>> search(String query) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> body = Map.of("query", query, "top_k", 5);
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<List> response = restTemplate.exchange(
//                PYTHON_API + "/predict",
//                HttpMethod.POST,
//                entity,
//                List.class
//        );
//
//        return response.getBody();
//    }
//
//    // ── Comparaison / meilleur produit ───────────────────────────
//    @SuppressWarnings("unchecked")
//    public Map<String, Object> compare(String query) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> body = Map.of("query", query, "top_k", 5);
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(
//                PYTHON_API + "/compare",
//                HttpMethod.POST,
//                entity,
//                Map.class
//        );
//
//        return response.getBody();
//    }
//    @SuppressWarnings("unchecked")
//    public List<Map<String, Object>> searchTopK(String query, int topK) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> body = Map.of("query", query, "top_k", topK);
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        ResponseEntity<List> response = restTemplate.exchange(
//                PYTHON_API + "/predict",
//                HttpMethod.POST,
//                entity,
//                List.class
//        );
//        return response.getBody();
//    }
//}