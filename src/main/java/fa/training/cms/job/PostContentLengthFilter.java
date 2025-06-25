package fa.training.cms.job;


import fa.training.cms.entity.Post;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
@Component
public class PostContentLengthFilter implements ItemProcessor<Post, Post> {
    private static final int MAX_CONTENT_LENGTH = 500;
    @Override
    public Post process(Post post) throws Exception {
        if (post.getContent() != null && post.getContent().length() <= MAX_CONTENT_LENGTH) {
            return post; // Giữ lại Post nếu content <= 500 ký tự
        }
        return null; // Bỏ qua Post nếu content dài hơn 500 ký tự
    }
}