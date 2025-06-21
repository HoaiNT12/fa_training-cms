package fa.training.cms.repository;

import fa.training.cms.entity.PostCategory;
import fa.training.cms.entity.PostCategoryEmbededId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategoryEmbededId> {
	List<PostCategory> findByCategoryId(Long categoryId);
}
