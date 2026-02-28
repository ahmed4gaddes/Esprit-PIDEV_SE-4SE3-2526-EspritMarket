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
    public Product updateProduct(Product product) {
        return irepositoryproduct.save(product) ;
    }

    @Override
    public Product getProductById(Long id) {
        return irepositoryproduct.findById(id).orElseThrow(() -> new NoSuchElementException("Prodcut not found with id: " + id));
    }

    @Override
    public  List<Product> getAllProducts() {
        return irepositoryproduct.findAll();
    }

    @Override
    public void deleteProduct(Long id) {
        irepositoryproduct.deleteById(id);

    }
}
