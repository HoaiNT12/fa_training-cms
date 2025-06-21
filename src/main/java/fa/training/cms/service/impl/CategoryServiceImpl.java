package fa.training.cms.service.impl;

import fa.training.cms.entity.Category;
import fa.training.cms.entity.PostCategory;
import fa.training.cms.repository.CategoryRepository;
import fa.training.cms.repository.PostCategoryRepository;
import fa.training.cms.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final PostCategoryRepository postCategoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, PostCategoryRepository postCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.postCategoryRepository = postCategoryRepository;
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
    @Transactional
    public void inactivate(Long id) throws EntityNotFoundException{
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            Category existingCategory = category.get();
            existingCategory.setActive(false);
            List<PostCategory> postCategories = postCategoryRepository.findByCategoryId(existingCategory.getId());
            postCategories.forEach(s -> s.setDeleted(true));
            categoryRepository.save(existingCategory);
            postCategoryRepository.saveAll(postCategories);
        }
        else {
            throw new EntityNotFoundException("Category not found with id " + id);
        }
    }
}
