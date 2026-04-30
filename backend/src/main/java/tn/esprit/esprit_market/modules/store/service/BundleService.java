package tn.esprit.esprit_market.modules.store.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.order.repository.OrderItemRepository;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.Normalizer;
@Service
@AllArgsConstructor
public class BundleService {

    private OrderItemRepository orderItemRepository;

    private IRepositoryProduct productRepository;

    public List<Map<String, Object>> getBundleSuggestions(Long productId) {
        List<Object[]> results = orderItemRepository
                .findFrequentlyBoughtTogether(productId);

        List<Map<String, Object>> bundles = new ArrayList<>();

        for (Object[] row : results) {
            Long relatedProductId = ((Number) row[0]).longValue();
            Long frequency        = ((Number) row[1]).longValue();

            productRepository.findById(relatedProductId).ifPresent(product -> {
                bundles.add(mapProductToBundle(product, frequency));
            });
        }

        // --- FALLBACK: complementary products based on product name + category keywords ---
        if (bundles.isEmpty()) {
            productRepository.findById(productId).ifPresent(p -> {
                // Determine product type from both name AND category name
                String productText = p.getName().toLowerCase();
                String categoryText = (p.getCategory() != null && p.getCategory().getName() != null)
                        ? p.getCategory().getName().toLowerCase() : "";
                String combined = productText + " " + categoryText;

                List<String> complementaryCategoryKeywords = getComplementaryCategoryKeywords(combined);
                List<String> complementaryNameKeywords     = getComplementaryNameKeywords(combined);

                if (!complementaryCategoryKeywords.isEmpty() || !complementaryNameKeywords.isEmpty()) {
                    productRepository.findAll().stream()
                        .filter(other -> !other.getId().equals(productId) && other.getStock() > 0)
                        .filter(other -> {
                            // Normalize the OTHER product's name and category (strip accents)
                            String otherName = normalize(other.getName());
                            String otherCat  = (other.getCategory() != null && other.getCategory().getName() != null)
                                    ? normalize(other.getCategory().getName()) : "";
                            // Match on category name of the OTHER product
                            boolean catMatch  = complementaryCategoryKeywords.stream()
                                    .anyMatch(kw -> otherCat.contains(normalize(kw)) || otherName.contains(normalize(kw)));
                            // Match on product name of the OTHER product
                            boolean nameMatch = complementaryNameKeywords.stream()
                                    .anyMatch(kw -> otherName.contains(normalize(kw)) || otherCat.contains(normalize(kw)));
                            boolean result = catMatch || nameMatch;
                            System.out.println("  [Bundle] Checking: " + other.getName()
                                + " | catMatch=" + catMatch + " nameMatch=" + nameMatch);
                            return result;
                        })
                        .limit(6)
                        .forEach(other -> bundles.add(mapProductToBundle(other, 0L)));
                }

                // Final fallback: same-store products
                if (bundles.isEmpty() && p.getStore() != null) {
                    productRepository.findAll().stream()
                        .filter(other -> !other.getId().equals(productId)
                                && other.getStore() != null
                                && other.getStore().getId().equals(p.getStore().getId())
                                && other.getStock() > 0)
                        .limit(4)
                        .forEach(other -> bundles.add(mapProductToBundle(other, 0L)));
                }
            });
        }

        return bundles;
    }

    /**
     * Returns complementary CATEGORY keywords to match against other products' categories.
     * Example: laptop → ["accessoire", "peripherique", "peripheral", "gaming", "audio"]
     */
    private List<String> getComplementaryCategoryKeywords(String combined) {
        List<String> keywords = new java.util.ArrayList<>();

        // PC / Laptop
        if (matches(combined, "laptop", "pc", "ordinateur", "notebook", "macbook",
                    "asus", "dell", "lenovo", "hp", "acer", "msi", "rog", "probook", "strix")) {
            keywords.addAll(java.util.Arrays.asList(
                "accessoire", "accessory", "peripherique", "peripheral",
                "audio", "gaming", "clavier", "keyboard", "mouse", "souris",
                "sac", "bag", "ecran", "monitor", "hub", "usb"
            ));
        }
        // Smartphone / Phone
        else if (matches(combined, "phone", "smartphone", "iphone", "samsung", "galaxy",
                         "redmi", "xiaomi", "vivo", "oppo", "infinix", "mobile")) {
            keywords.addAll(java.util.Arrays.asList(
                "accessoire", "accessory", "coque", "case", "chargeur", "charger",
                "audio", "protection", "ecouteur", "earphone", "airpods"
            ));
        }
        // Ecran / Monitor
        else if (matches(combined, "ecran", "monitor", "display", "screen")) {
            keywords.addAll(java.util.Arrays.asList(
                "accessoire", "peripherique", "clavier", "keyboard", "souris", "mouse", "cable", "hub"
            ));
        }

        return keywords;
    }

    /**
     * Returns complementary PRODUCT NAME keywords to match against other products' names.
     * Example: laptop → ["souris", "clavier", "casque", ...]
     */
    private List<String> getComplementaryNameKeywords(String combined) {
        List<String> keywords = new java.util.ArrayList<>();

        // PC / Laptop
        if (matches(combined, "laptop", "pc", "ordinateur", "notebook", "macbook",
                    "asus", "dell", "lenovo", "hp", "acer", "msi", "rog", "probook", "strix")) {
            keywords.addAll(java.util.Arrays.asList(
                "souris", "clavier", "mouse", "keyboard", "casque", "headset",
                "sac", "bag", "ecran", "monitor", "usb", "hub", "webcam", "redragon",
                "gaming", "pad", "tapis"
            ));
        }
        // Smartphone / Phone
        else if (matches(combined, "phone", "smartphone", "iphone", "samsung", "galaxy",
                         "redmi", "xiaomi", "vivo", "oppo", "infinix", "mobile")) {
            keywords.addAll(java.util.Arrays.asList(
                "coque", "case", "chargeur", "charger", "ecouteur", "earphone",
                "airpods", "film", "protecteur", "power bank", "powerbank", "cable"
            ));
        }
        // Ecran / Monitor
        else if (matches(combined, "ecran", "monitor", "display", "screen")) {
            keywords.addAll(java.util.Arrays.asList(
                "clavier", "keyboard", "souris", "mouse", "hdmi", "cable", "hub", "webcam"
            ));
        }
        // Casque / Headset
        else if (matches(combined, "casque", "headset", "headphone", "earphone", "airpods", "ecouteur")) {
            keywords.addAll(java.util.Arrays.asList(
                "microphone", "cable", "amplifier", "stand", "support", "adaptateur"
            ));
        }
        // Clavier / Keyboard
        else if (matches(combined, "clavier", "keyboard")) {
            keywords.addAll(java.util.Arrays.asList(
                "souris", "mouse", "pad", "tapis", "redragon", "gaming"
            ));
        }
        // Souris / Mouse
        else if (matches(combined, "souris", "mouse", "redragon")) {
            keywords.addAll(java.util.Arrays.asList(
                "tapis", "pad", "clavier", "keyboard", "hub", "usb"
            ));
        }
        // Imprimante
        else if (matches(combined, "imprimante", "printer", "scanner")) {
            keywords.addAll(java.util.Arrays.asList(
                "encre", "ink", "cartouche", "papier", "paper", "cable", "usb"
            ));
        }
        // Caméra
        else if (matches(combined, "camera", "appareil photo", "gopro", "canon", "nikon")) {
            keywords.addAll(java.util.Arrays.asList(
                "objectif", "lens", "tripied", "tripod", "sac", "bag", "carte", "card", "sd"
            ));
        }

        return keywords;
    }

    /**
     * Accent-insensitive and case-insensitive keyword matching.
     * "écouteur".contains("ecouteur") = true after normalization.
     */
    private boolean matches(String name, String... keywords) {
        String normalized = normalize(name);
        for (String kw : keywords) {
            if (normalized.contains(normalize(kw))) return true;
        }
        return false;
    }

    /** Strips accents: é→e, è→e, â→a, ç→c, etc. */
    private String normalize(String input) {
        if (input == null) return "";
        String decomposed = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private Map<String, Object> mapProductToBundle(Product product, Long frequency) {
        Map<String, Object> bundle = new HashMap<>();
        bundle.put("productId",   product.getId());
        bundle.put("name",        product.getName());
        bundle.put("price",       product.getPrice());
        bundle.put("stock",       product.getStock());
        bundle.put("imageUrl",    product.getImageUrl());
        bundle.put("description", product.getDescription());
        bundle.put("frequency",   frequency);
        return bundle;
    }

    // Pour le chatbot — bundles par nom de produit
    public List<Map<String, Object>> getBundlesByProductName(String productName) {
        return productRepository
                .findByNameContainingIgnoreCase(productName)
                .stream()
                .findFirst()
                .map(p -> getBundleSuggestions(p.getId()))
                .orElse(List.of());
    }

    // Tous les bundles populaires
    public List<Map<String, Object>> getAllPopularBundles() {
        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> allBundles = new ArrayList<>();

        for (Product p : products) {
            List<Map<String, Object>> suggestions = getBundleSuggestions(p.getId());
            if (!suggestions.isEmpty()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("product",     p.getName());
                entry.put("productId",   p.getId());
                entry.put("suggestions", suggestions);
                allBundles.add(entry);
            }
        }
        return allBundles;
    }
}
