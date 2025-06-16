package fa.training.cms.service;

import fa.training.cms.entity.Category;
import fa.training.cms.repository.CategoryRepository;
import fa.training.cms.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(
                new Category(1L, "Category1", true, null),
                new Category(2L, "Category2", true, null)
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryServiceImpl.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }
	private Category category;
    @BeforeEach
    void setUp() {
        category = new Category(1L, "Category1", true, null);
    }

    @Test
    void findById_ShouldReturnCategoryWhenIdExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryServiceImpl.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnNullWhenIdDoesNotExist() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Category result = categoryServiceImpl.findById(1L);

        // Assert
        assertNull(result);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void insert_ShouldSaveAndReturnCategory() {
        // Arrange
        Category category = new Category(null, "New Category", true, null);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category result = categoryServiceImpl.insert(category);

        // Assert
        assertNotNull(result);
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void update_ShouldUpdateAndReturnCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category result = categoryServiceImpl.update(category);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void update_ShouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.update(category));
        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void inactivate_ShouldInactivateCategoryWhenIdExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        categoryServiceImpl.inactivate(1L);

        // Assert
        assertFalse(category.isActive());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void inactivate_ShouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryServiceImpl.inactivate(1L));
        assertEquals("Category not found with id 1", exception.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).save(any());
    }
}