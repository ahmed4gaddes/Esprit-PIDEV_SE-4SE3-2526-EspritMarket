package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.entity.Product;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {
    private IRepositoryCategory iRepositoryCategory;
   // private IRepositoryProduct iRepositoryProduct;
    @Override
    public Category addCategory(Category category) {

        //Product product = iRepositoryProduct.findById(category.getProduct().getId())
          //      .orElseThrow(() -> new EntityNotFoundException("product introuvable"));
        //category.setProduct(product);
        return iRepositoryCategory.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        return iRepositoryCategory.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return iRepositoryCategory.findById(id).get();
    }

    @Override
    public List<Category> getAllCategory() {
        return iRepositoryCategory.findAll();
    }

    @Override
    public void deleteCategory(Category category) {
        iRepositoryCategory.delete(category);

    }
}
