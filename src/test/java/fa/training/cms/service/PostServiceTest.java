package fa.training.cms.service;

import fa.training.cms.entity.*;
import fa.training.cms.repository.CategoryRepository;
import fa.training.cms.repository.PostCategoryRepository;
import fa.training.cms.repository.PostRepository;
import fa.training.cms.service.dto.CategoryDto;
import fa.training.cms.service.dto.PostDto;
import fa.training.cms.service.enums.Role;
import fa.training.cms.service.enums.Status;
import fa.training.cms.service.impl.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;// = Mockito.mock(PostRepository.class);
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PostCategoryRepository postCategoryRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostServiceImpl postService;
    //private PostService postService = new PostServiceImpl(postRepository,categoryRepository,modelMapper);
    private User user;
    private List<CategoryDto> categoryIds;
    private List<PostCategory> postCategories;
    private Post post;
    private PostDto postDto;
    private List<Category> categorys;

    public static Stream<Arguments> getPostByIdArguments(){
        User user = new User(1L,"Hoai","adsds", Role.EDITOR,true,null,null);
        Post post = new Post(1L, "Test post", "Test Content", Status.PUBLISHED,null,user);
        Category category1 = new Category(1L,"Category 1", true,null);
        Category category2 = new Category(2L,"Category 2", true,null);
        List<PostCategory> postCategories = new ArrayList<>();

        postCategories.add(new PostCategory(new PostCategoryEmbededId(1L,1L), category1, post));
        postCategories.add(new PostCategory(new PostCategoryEmbededId(2L,1L), category2, post));

        post.setPostCategories(postCategories);
        category1.setPostCategories(postCategories);
        category2.setPostCategories(postCategories);
        //dto
        List<CategoryDto> categoryIds = new ArrayList<>();
        categoryIds.add(new CategoryDto(1L,"Category 1"));
        categoryIds.add(new CategoryDto(2L,"Category 2"));

        PostDto postDto = new PostDto(1L, "Test post", "Test Content", Status.PUBLISHED, categoryIds);
        return Stream.of(
                Arguments.of(null,null,false,1,0),
                Arguments.of(post,postDto,true,1,1)
        );
    }

    @BeforeEach
    void setUp() {
        //entity
        user = new User(1L,"Hoai","adsds", Role.EDITOR,true,null,null);
        post = new Post(1L, "Test post", "Test Content", Status.PUBLISHED,null,user);
        Category category1 = new Category(1L,"Category 1", true,null);
        Category category2 = new Category(2L,"Category 2", true,null);
        postCategories = new ArrayList<>();

        postCategories.add(new PostCategory(new PostCategoryEmbededId(1L,1L), category1, post));
        postCategories.add(new PostCategory(new PostCategoryEmbededId(2L,1L), category2, post));

		post.setPostCategories(postCategories);
		category1.setPostCategories(postCategories);
		category2.setPostCategories(postCategories);

        categorys = new ArrayList<>();
        categorys.add(category1);
        categorys.add(category2);
        //dto
        categoryIds = new ArrayList<>();
        categoryIds.add(new CategoryDto(1L,"Category 1"));
        categoryIds.add(new CategoryDto(2L,"Category 2"));

        postDto = new PostDto(1L, "Test post", "Test Content", Status.PUBLISHED, categoryIds);
    }


    @Test
    void findAllPublishedPost() {
        //Arrange
        when(postRepository.findAllPublishedPost()).thenReturn(Arrays.asList(post));
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(postDto);

        //Act
        List<PostDto> result = postService.findAllPublishedPost();

        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findAllPublishedPost();
        verify(modelMapper).map(post, PostDto.class);
    }

    @ParameterizedTest
    @MethodSource("getPostByIdArguments")
    void postDetail(Post post, PostDto postDto, boolean assertPostFound, int verifyPostRepo, int verifyMapper) {
        long postId = 1L;
        lenient().when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);
		when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post));

        var result = postService.postDetail(postId);

        assertEquals(assertPostFound, result.isPresent());
        verify(postRepository, times(verifyPostRepo)).findById(postId);
        verify(modelMapper, times(verifyMapper)).map(post, PostDto.class);
    }

    @Test
    void create() {
        doAnswer( inv ->
                {
                    PostDto src = inv.getArgument(0);
                    Post dest = inv.getArgument(1);
                    dest.setId(src.getId());
                    dest.setTitle(src.getTitle());
                    dest.setPostCategories(src.getCategoryIds().stream()
                            .map(pc -> {
                                var pc1 = postCategories.stream().filter(p -> p.getId().getCategory_id() == pc.getId()).findFirst();
                                return pc1.get();
                            })
                            .collect(Collectors.toList()));
                    return null;
                }
        ).when(modelMapper).map(any(PostDto.class), any(Post.class));
        when(categoryRepository.findAllById(any())).thenReturn(categorys);
        when(postRepository.save(any())).thenReturn(post);
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        var insertedPost = postService.create(postDto, user);

        assertNotNull(insertedPost);
        assertEquals(postDto.getId(),insertedPost.getId());
        assertEquals(postDto.getTitle(),insertedPost.getTitle());
        assertEquals(2, insertedPost.getCategoryIds().size());

        verify(postRepository, times(1)).save(any());
        verify(categoryRepository, times(1)).findAllById(any());
    }

    @Test
    void update_HappyCase() {
        // AAA pattern
        //Arrange set up data, dependencies, mocks
        CategoryDto updatedCategoryDto = new CategoryDto(1L,"Category 1");
        Category updatedCategory = new Category(1L, "Category 1", true, null);
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .title("Updated tile")
                .content("Updated content")
                .categoryIds(List.of(updatedCategoryDto))
                .build();
        Post updatedPost = new Post(1L, "Updated tile", "Updated content", Status.PUBLISHED, null, user);
        PostCategory updatedPostCategory = new PostCategory(new PostCategoryEmbededId(1L,1L),updatedCategory,updatedPost);
		updatedCategory.setPostCategories(List.of(updatedPostCategory));
        updatedPost.setPostCategories(List.of(updatedPostCategory));

        //stubbing dependencies (assume dependencies are well-behavior)
        doAnswer(inv ->{
            PostDto src = inv.getArgument(0);
            Post dest = inv.getArgument(1);
            dest.setId(src.getId());
            dest.setUser(user);
            dest.setTitle(src.getTitle());
            dest.setContent(src.getContent());
            dest.setPostCategories(src.getCategoryIds().stream()
                    .map(s ->{
                        var pc = postCategories.stream().filter(p-> p.getId().getCategory_id() == s.getId()).findFirst();
                        return pc.get();
                    })
                    .collect(Collectors.toList()));
            dest.setStatus(src.getStatus());
            return null;
        }).when(modelMapper).map(any(PostDto.class), any(Post.class));


        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        lenient().when(categoryRepository.findAllById(any())).thenReturn(List.of(updatedCategory));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(updatedPostDto);

        //Act: execute the method under test
        PostDto updatedDto = postService.update(postDto);
        //Assert: verify the result of method under test
        //Check behavior + interaction with dependencies
        assertNotNull(updatedDto);
        assertEquals(updatedPostDto.getTitle(), updatedDto.getTitle());
        assertEquals(1, updatedDto.getCategoryIds().size());
        //check interaction of dependencies
        verify(postRepository,Mockito.times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void update_PostNotFound(){
		when(postRepository.findById(any())).thenReturn(Optional.empty());

        //var result = postService.update(postDto);

        assertThrows(EntityNotFoundException.class, () -> postService.update(postDto));

        verify(postRepository,Mockito.times(1)).findById(1L);
        verify(categoryRepository,times(0)).findAllById(any());
        verify(postRepository, times(0)).save(post);
        verify(modelMapper, times(0)).map(any(Post.class), any(PostDto.class));
    }

    @Test
    void softDelete() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postCategoryRepository.findByPostId(1L)).thenReturn(List.of());
        postService.softDelete(1L);

        assertTrue(post.isDeleted());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
    }
}