package fa.training.cms.service.impl;

import fa.training.cms.entity.*;
import fa.training.cms.repository.CategoryRepository;
import fa.training.cms.repository.PostCategoryRepository;
import fa.training.cms.repository.PostRepository;
import fa.training.cms.service.PostService;
import fa.training.cms.service.dto.CategoryDto;
import fa.training.cms.service.dto.PostDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, PostCategoryRepository postCategoryRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.postCategoryRepository = postCategoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PostDto> findAllPublishedPost() {
        List<Post> posts = postRepository.findAllPublishedPost();
        List<PostDto> postDtos = posts.stream()
                .map(post->modelMapper.map(post,PostDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    @Override
    public Optional<PostDto> postDetail(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()) {
            Post savedPost = post.get();
            PostDto savedPostDto = modelMapper.map(savedPost, PostDto.class);
            List<CategoryDto> savedCategoryDtos = savedPost.getPostCategories().stream()
                    .map(PostCategory::getCategory)
                    .map(category -> modelMapper.map(category, CategoryDto.class))
                    .collect(Collectors.toList());
            savedPostDto.setCategoryIds(savedCategoryDtos);
            return Optional.of(savedPostDto);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public PostDto create(PostDto postDto, User user) {
        Post post = new Post();
        modelMapper.map(postDto, post);
		post.setUser(user);
        List<PostCategory> postCategories = new ArrayList<>();
        if (postDto.getCategoryIds() != null && !postDto.getCategoryIds().isEmpty()) {
            HashSet<Long> categoryIds = postDto.getCategoryIds().stream()
                    .map(CategoryDto::getId)
                    .collect(Collectors.toCollection(HashSet::new));
            List<Category> categories = categoryRepository.findAllById(categoryIds);

            if (categories.size() != categoryIds.size()) {
                System.err.println("Warning: Some category IDs provided were not found.");
            }

            for (Category category : categories) {
                PostCategory postCategory = new PostCategory();

                PostCategoryEmbededId embededId = new PostCategoryEmbededId(post.getId(), category.getId()); // Note: post.getId() might be null until flush/save, may need alternative for composite key before save. Let's rely on JPA persistence context.

                postCategory.setId(embededId);
                postCategory.setPost(post);
                postCategory.setCategory(category);
                postCategories.add(postCategory);
            }
        }
        post.setPostCategories(postCategories);
        Post savedPost = postRepository.save(post);
        PostDto savedPostDto = modelMapper.map(savedPost, PostDto.class);
        List<CategoryDto> savedCategoryDtos = savedPost.getPostCategories().stream()
                .map(PostCategory::getCategory)
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
        savedPostDto.setCategoryIds(savedCategoryDtos);
        return savedPostDto;
    }

    @Override
    @Transactional
    public PostDto update(PostDto postDto) throws EntityNotFoundException{
        if (postDto.getId() == null) {
            throw new IllegalArgumentException("PostDto must have an ID for update.");
        }
        Post existingPost = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postDto.getId()));
        modelMapper.map(postDto, existingPost);

        List<PostCategory> existingPostCategories = existingPost.getPostCategories();
        List<CategoryDto> newCategoryDtos = postDto.getCategoryIds(); // List of CategoryDtos from the input

        Set<Long> currentCategoryIds = existingPostCategories.stream()
                .map(pc -> pc.getCategory().getId())
                .collect(Collectors.toSet());

        Set<Long> targetCategoryIds = newCategoryDtos.stream()
                .map(CategoryDto::getId) // Assuming CategoryDto has getId()
                .collect(Collectors.toSet());

        List<PostCategory> postCategoriesToRemove = existingPostCategories.stream()
                .filter(pc -> !targetCategoryIds.contains(pc.getCategory().getId()))
                .collect(Collectors.toList());

        List<Long> categoryIdsToAdd = targetCategoryIds.stream()
                .filter(categoryId -> !currentCategoryIds.contains(categoryId))
                .collect(Collectors.toList());

        existingPostCategories.removeAll(postCategoriesToRemove);

        if (!categoryIdsToAdd.isEmpty()) {
            List<Category> categoriesToAdd = categoryRepository.findAllById(categoryIdsToAdd);
            if (categoriesToAdd.size() != categoryIdsToAdd.size()) {
                System.err.println("Warning: Some new category IDs provided were not found.");
            }
            for (Category category : categoriesToAdd) {
                PostCategory postCategory = new PostCategory();
                PostCategoryEmbededId embededId = new PostCategoryEmbededId(existingPost.getId(), category.getId());
                postCategory.setId(embededId);
                postCategory.setPost(existingPost);
                postCategory.setCategory(category);
                existingPostCategories.add(postCategory);
            }
        }

        Post updatedPost = postRepository.save(existingPost);
        PostDto updatedPostDto = modelMapper.map(updatedPost, PostDto.class);
        List<CategoryDto> updatedCategoryDtos = updatedPost.getPostCategories().stream()
                .map(PostCategory::getCategory)
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
        updatedPostDto.setCategoryIds(updatedCategoryDtos);
        return updatedPostDto;
    }

    @Override
    public void softDelete(Long id) throws EntityNotFoundException {
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            Post existingPost = post.get();
            existingPost.setDeleted(true);
            List<PostCategory> postCategories = postCategoryRepository.findByPostId(existingPost.getId());
            postCategories.forEach(s -> s.setDeleted(true));
            postCategoryRepository.saveAll(postCategories);
            postRepository.save(existingPost);
        }
        else {
            throw new EntityNotFoundException("Post not found with id " + id);
        }
    }
}
