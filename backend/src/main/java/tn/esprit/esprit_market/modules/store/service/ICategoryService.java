package tn.esprit.esprit_market.modules.store.service;

import tn.esprit.esprit_market.modules.store.entity.Category;

import java.util.List;

public interface ICategoryService {
    Category addCategory(Category category);
    Category getCategoryById(Long id);
    List <Category> getAllCategory();
    void deleteCategory(Long id);
    Category updateCategory(Category category, Long id);
}
