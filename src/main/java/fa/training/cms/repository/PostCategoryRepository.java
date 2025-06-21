package fa.training.cms.repository;

import fa.training.cms.entity.PostCategory;
import fa.training.cms.entity.PostCategoryEmbededId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCategoryRepository extends JpaRepository<PostCategory, PostCategoryEmbededId> {
    @Query("SELECT pc FROM PostCategory pc WHERE pc.id.category_id = :categoryId")
    List<PostCategory> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT pc FROM PostCategory pc WHERE pc.id.post_id = :postId")
    List<PostCategory> findByPostId(@Param("postId") Long postId);
}
