package fa.training.cms.service;

import fa.training.cms.entity.Post;
import fa.training.cms.entity.User;
import fa.training.cms.service.dto.PostDto;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<PostDto> findAllPublishedPost();
    Optional<PostDto> postDetail(Long id);
    PostDto create(PostDto postDto, User user);
    PostDto update(PostDto postDto);
    void softDelete(Long id);
}
