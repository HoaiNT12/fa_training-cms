package fa.training.cms.service.impl;

import fa.training.cms.entity.Category;
import fa.training.cms.repository.CategoryRepository;
import fa.training.cms.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.orElse(null);
    }

    @Override
    public Category insert(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category update(Category category) throws EntityNotFoundException{
        if(category.getId() == null || !categoryRepository.existsById(category.getId())) {
            throw new EntityNotFoundException("Category not found");
        }
        category.setActive(true);
        return categoryRepository.save(category);
    }

    @Override
    public void inactivate(Long id) throws EntityNotFoundException{
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            Category existingCategory = category.get();
            existingCategory.setActive(false);
            categoryRepository.save(existingCategory);
        }
        else {
            throw new EntityNotFoundException("Category not found with id " + id);
        }
    }
}
