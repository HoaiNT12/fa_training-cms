package fa.training.cms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import fa.training.cms.service.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "post")
@Where(clause = "is_deleted = FALSE")
public class Post extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "VARCHAR(MAX)")
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PUBLISHED;

    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCategory> postCategories;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User user;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
