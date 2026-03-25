package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;

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
    public Category updateCategory(Category category, Long id) {
        Category existing = iRepositoryCategory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie introuvable avec id: " + id));

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setType(category.getType());

        return iRepositoryCategory.save(existing);
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
    public void deleteCategory(Long id) {
        iRepositoryCategory.deleteById(id);
    }

    @Override
    public List<Category> getCategoriesByStore(Long storeId) {
        return iRepositoryCategory.findByStoreId(storeId);
    }
}
