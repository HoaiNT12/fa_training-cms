package fa.training.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_category")
@Where(clause = "is_deleted = FALSE")
public class PostCategory extends Auditable{
	@EmbeddedId
    private PostCategoryEmbededId id;

    @ManyToOne
    @MapsId("category_id")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @MapsId("post_id")
    @JoinColumn(name = "post_id")
    private Post post;

    @Override
    public String toString() {
        return "PostCategory{" +
                "id=" + id +
                '}';
    }
}
