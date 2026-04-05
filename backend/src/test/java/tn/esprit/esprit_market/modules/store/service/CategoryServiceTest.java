package tn.esprit.esprit_market.modules.store.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.enums.CategoryType;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryCategory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private IRepositoryCategory iRepositoryCategory;

    @InjectMocks
    private CategoryService categoryService;

    private Category fakeCategory;

    @BeforeEach
    void setUp() {
        fakeCategory = new Category();
        fakeCategory.setId(1L);
        fakeCategory.setName("Électronique");
        fakeCategory.setDescription("Appareils électroniques");
        fakeCategory.setType(CategoryType.TECH);
    }

    // ==================== addCategory ====================
    @Test
    void testAddCategory_Success() {
        when(iRepositoryCategory.save(any(Category.class))).thenReturn(fakeCategory);

        Category result = categoryService.addCategory(fakeCategory);

        assertNotNull(result);
        assertEquals("Électronique", result.getName());
        verify(iRepositoryCategory, times(1)).save(any(Category.class));
    }

    // ==================== getCategoryById ====================
    @Test
    void testGetCategoryById_Found() {
        when(iRepositoryCategory.findById(1L)).thenReturn(Optional.of(fakeCategory));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Électronique", result.getName());
    }

    @Test
    void testGetCategoryById_NotFound_ThrowsException() {
        when(iRepositoryCategory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> categoryService.getCategoryById(99L));
    }

    // ==================== getAllCategory ====================
    @Test
    void testGetAllCategory_ReturnsList() {
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Vêtements");
        when(iRepositoryCategory.findAll()).thenReturn(Arrays.asList(fakeCategory, cat2));

        List<Category> result = categoryService.getAllCategory();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ==================== updateCategory ====================
    @Test
    void testUpdateCategory_Success() {
        Category updatedData = new Category();
        updatedData.setName("High-Tech");
        updatedData.setDescription("Gadgets modernes");
        updatedData.setType(CategoryType.TECH);

        when(iRepositoryCategory.findById(1L)).thenReturn(Optional.of(fakeCategory));
        when(iRepositoryCategory.save(any(Category.class))).thenReturn(fakeCategory);

        Category result = categoryService.updateCategory(updatedData, 1L);

        assertNotNull(result);
        verify(iRepositoryCategory, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory_NotFound_ThrowsException() {
        when(iRepositoryCategory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(new Category(), 99L));
    }

    // ==================== deleteCategory ====================
    @Test
    void testDeleteCategory_Success() {
        doNothing().when(iRepositoryCategory).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(iRepositoryCategory, times(1)).deleteById(1L);
    }

    // ==================== getCategoriesByStore ====================
    @Test
    void testGetCategoriesByStore_ReturnsList() {
        when(iRepositoryCategory.findByStoreId(1L)).thenReturn(Arrays.asList(fakeCategory));

        List<Category> result = categoryService.getCategoriesByStore(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
