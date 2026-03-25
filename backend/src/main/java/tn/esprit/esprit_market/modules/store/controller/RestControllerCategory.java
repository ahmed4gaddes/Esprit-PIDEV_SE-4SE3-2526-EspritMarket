
package tn.esprit.esprit_market.modules.store.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.esprit_market.modules.store.dto.CategoryDTO;
import tn.esprit.esprit_market.modules.store.mapper.CategoryMapper;
import tn.esprit.esprit_market.modules.store.service.ICategoryService;
import tn.esprit.esprit_market.modules.store.entity.Category;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("category")
@AllArgsConstructor
public class RestControllerCategory {
private ICategoryService iCategoryService;
    private CategoryMapper categoryMapper;
    @PostMapping("addcategory")
    public Category addCategory(@RequestBody Category category) {
        return iCategoryService.addCategory(category);
    }

    @PutMapping("updateCategory/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setType(dto.getType());

        Category updated = iCategoryService.updateCategory(category, id);
        Category full    = iCategoryService.getCategoryById(updated.getId());
        return categoryMapper.toDTO(full);
    }

    // ✅ GET par id → retourne DTO
    @GetMapping("get/{id}")
    public CategoryDTO getCategoryById(@PathVariable Long id) {
        Category category = iCategoryService.getCategoryById(id);
        return categoryMapper.toDTO(category);  // ✅ toDTO
    }
    // ✅ GET all → retourne DTO
    @GetMapping("getall")
    public List<CategoryDTO> getAllCategory() {
        return iCategoryService.getAllCategory()
                .stream()
                .map(categoryMapper::toDTO)     // ✅ toDTO
                .collect(Collectors.toList());
    }

    @DeleteMapping("delete/{id}")
    public void deleteCategory(@PathVariable Long id) {
        iCategoryService.deleteCategory(id);
    }

    // ✅ GET par storeId → retourne DTO
    @GetMapping("by-store/{storeId}")
    public List<CategoryDTO> getCategoriesByStore(@PathVariable Long storeId) {
        return iCategoryService.getCategoriesByStore(storeId)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}

