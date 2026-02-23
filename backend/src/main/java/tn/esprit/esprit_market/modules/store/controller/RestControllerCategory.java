package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.service.ICategoryService;
import tn.esprit.esprit_market.modules.store.entity.Category;

import java.util.List;

@RestController
@RequestMapping("category")
@AllArgsConstructor
public class RestControllerCategory {
private ICategoryService iCategoryService;
    @PostMapping("addcategory")
    public Category addCategory(@RequestBody Category category) {
        return iCategoryService.addCategory(category);
    }

    @PutMapping("updateCategory")
    public Category updateCategory(@RequestBody Category category) {
        return iCategoryService.updateCategory(category);
    }

    @GetMapping("get/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return iCategoryService.getCategoryById(id);
    }

    @GetMapping("getall")
    public List<Category> getAllCategory() {
        return iCategoryService.getAllCategory();
    }

    @DeleteMapping("delete")
    public void deleteCategory(@RequestBody Category category) {
        iCategoryService.deleteCategory(category);
    }
}

