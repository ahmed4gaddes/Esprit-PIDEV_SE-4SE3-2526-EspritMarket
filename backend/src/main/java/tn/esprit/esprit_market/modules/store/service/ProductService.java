package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.entity.Store;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryStore;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ProductService implements IproductService {
    private IRepositoryProduct irepositoryproduct;
    private IRepositoryStore iRepositoryStore;
    private IRepositoryCategory iRepositoryCategory;
    @Override
    public Product addProduct(Product product) {
        Store store = iRepositoryStore.findById(product.getStore().getId())
                .orElseThrow(() -> new EntityNotFoundException("Store introuvable"));

            Category category = iRepositoryCategory.findById(product.getCategory().getId())
                .orElseThrow(() -> new EntityNotFoundException("Category introuvable"));
        product.setStore(store);
        product.setCategory(category);
        return  irepositoryproduct.save(product);
    }

    @Override
    public Product updateProduct(Product product, Long id) {
        // ✅ Vérifier que le produit existe
        Product existing = irepositoryproduct.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + id));

        // ✅ Mettre à jour les champs
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setStock(product.getStock());
        existing.setActive(product.isActive());
        existing.setStore(product.getStore());
        existing.setCategory(product.getCategory());

        // ✅ Sauvegarder
        return irepositoryproduct.save(existing);
    }

    @Override
    public Product getProductById(Long id) {
        return irepositoryproduct.findById(id).orElseThrow(() -> new NoSuchElementException("Prodcut not found with id: " + id));
    }

    @Override
    public Product getProductByName(String name) {
        if (name == null || name.isBlank()) {
            throw new NoSuchElementException("Product not found: empty name");
        }
        return irepositoryproduct.findFirstByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new NoSuchElementException("Product not found with name: " + name));
    }

    @Override
    public  List<Product> getAllProducts() {
        return irepositoryproduct.findAll();
    }

    @Override
    public void deleteProduct(Long id) {
        try {
            irepositoryproduct.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new tn.esprit.esprit_market.exceptions.UserException("Impossible de supprimer ce produit car il est déjà lié à des commandes ou paniers. Veuillez le désactiver à la place.");
        }
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return irepositoryproduct.existsByNameIgnoreCase(name.trim());
    }
}
