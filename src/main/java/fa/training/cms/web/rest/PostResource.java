package fa.training.cms.web.rest;

import fa.training.cms.entity.User;
import fa.training.cms.service.PostService;
import fa.training.cms.service.UserService;
import fa.training.cms.service.dto.PostDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostResource {
    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostResource(PostService postService, ModelMapper modelMapper, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> findAllPublishedPost(){
        List<PostDto> posts = postService.findAllPublishedPost();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> postDetail(@PathVariable Long id){
        Optional<PostDto> post = postService.postDetail(id);
        if(post.isPresent()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post.get());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PostDto> insert(@RequestBody PostDto postDto, @AuthenticationPrincipal User user){
		PostDto insertedPost = postService.create(postDto,user);
        return ResponseEntity.created(URI.create("/api/post/" + postDto.getId())).body(insertedPost);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<PostDto> update(@RequestBody PostDto postDto){
		try{
            PostDto updatedPost = postService.update(postDto);
            return ResponseEntity.ok(updatedPost);
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

	@PutMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id){
        try{
            postService.softDelete(id);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }
}
