package fa.training.cms.web.rest;

import fa.training.cms.entity.Category;
import fa.training.cms.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryResource {
    private final CategoryService categoryService;

    @Autowired
    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<List<Category>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
	public ResponseEntity<Category> findById(@PathVariable Long id){
        Category category = categoryService.findById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping()
    public ResponseEntity<Category> create(@RequestBody Category category){
        Category insertedCategory = categoryService.insert(category);
        return ResponseEntity.created(URI.create("/api/category/" + insertedCategory.getId())).body(insertedCategory);
    }

    @PutMapping()
    public ResponseEntity<Category> update(@RequestBody Category category){
        try{
            Category updatedCategory = categoryService.update(category);
            return ResponseEntity.ok(updatedCategory);
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        try {
            categoryService.inactivate(id); // Calls the corresponding service method
            return ResponseEntity.noContent().build(); // Returns 204 No Content on successful deletion
        }
        catch (EntityNotFoundException e)
        {
            return  ResponseEntity.notFound().build(); // Returns 404 Not Found if service throws EntityNotFoundException
        }
    }
}
