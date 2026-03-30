package tn.esprit.esprit_market.modules.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.esprit_market.modules.store.dto.CategoryDTO;
import tn.esprit.esprit_market.modules.store.entity.Category;
import tn.esprit.esprit_market.modules.store.enums.CategoryType;
import tn.esprit.esprit_market.modules.store.mapper.CategoryMapper;
import tn.esprit.esprit_market.modules.store.service.ICategoryService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestControllerCategoryTest {

    @Mock
    private ICategoryService iCategoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private RestControllerCategory controller;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronique");
        category.setDescription("Appareils");
        category.setType(CategoryType.TECH);

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronique");
        categoryDTO.setDescription("Appareils");
        categoryDTO.setType(CategoryType.TECH);
        categoryDTO.setStoreId(1L);
    }

    @Test
    void testAddCategory() {
        when(iCategoryService.addCategory(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = controller.addCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Electronique", result.getName());
        verify(iCategoryService).addCategory(any(Category.class));
    }

    @Test
    void testAddCategory_WithoutStoreId() {
        categoryDTO.setStoreId(null);
        when(iCategoryService.addCategory(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = controller.addCategory(categoryDTO);

        assertNotNull(result);
        verify(iCategoryService).addCategory(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        when(iCategoryService.updateCategory(any(Category.class), eq(1L))).thenReturn(category);
        when(iCategoryService.getCategoryById(1L)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = controller.updateCategory(1L, categoryDTO);

        assertNotNull(result);
        assertEquals("Electronique", result.getName());
    }

    @Test
    void testGetCategoryById() {
        when(iCategoryService.getCategoryById(1L)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        CategoryDTO result = controller.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetAllCategory() {
        when(iCategoryService.getAllCategory()).thenReturn(Arrays.asList(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        List<CategoryDTO> results = controller.getAllCategory();

        assertEquals(1, results.size());
        verify(iCategoryService).getAllCategory();
    }

    @Test
    void testDeleteCategory() {
        doNothing().when(iCategoryService).deleteCategory(1L);

        controller.deleteCategory(1L);

        verify(iCategoryService).deleteCategory(1L);
    }

    @Test
    void testGetCategoriesByStore() {
        when(iCategoryService.getCategoriesByStore(1L)).thenReturn(Arrays.asList(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        List<CategoryDTO> results = controller.getCategoriesByStore(1L);

        assertEquals(1, results.size());
        verify(iCategoryService).getCategoriesByStore(1L);
    }
}
