package fa.training.cms.service;

import fa.training.cms.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    Category findById(Long id);
    Category insert(Category category);
    Category update(Category category);
    void inactivate(Long id);
}
