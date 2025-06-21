package fa.training.cms.web.rest;
import fa.training.cms.security.dto.LoginDto;
import fa.training.cms.service.PostService;
import fa.training.cms.service.UserService;
import fa.training.cms.service.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class PostResourceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Create a mock User object as the Principal
        LoginDto user = new LoginDto();
        user.setUsername("alice");
        user.setPassword("password1");
        // Set roles or authorities as needed (based on SecurityConfig requirements)
        // Assuming a method or field to set authorities/roles in User entity
        // For simplicity, directly setting authorities if your User class supports it
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.singletonList(() -> Role.ADMIN.name())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test
    @WithMockUser
    void shouldReturnAllPublishedPosts() throws Exception {
        mockMvc.perform(get("/api/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void shouldReturnPostDetails_whenPostExists() throws Exception {
        // Assuming a post ID exists in the database
        Long postId = 2L;
        mockMvc.perform(get("/api/post/" + postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId));
    }
    @Test
    @WithMockUser
    void shouldReturnNotFound_whenPostDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/post/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldInsertPost() throws Exception {
        String newPost = """
        {
                       "title": "AI",
                       "content": "Welcome to the world of technology",
                       "status": "PUBLISHED",
                       "categoryIds": [
                         {
                           "id": 1,
                           "name": "Technology"
                         },
                         {
                           "id": 4,
                           "name": "Science"
                         }
                       ]
                     }
    """;
        mockMvc.perform(post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPost))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("AI"));
    }
    @Test
    void shouldUpdatePost() throws Exception {
        String updatedPost = """
        {
        			   "id": 2,
                       "title": "AI",
                       "content": "Welcome to the world of technology",
                       "status": "PUBLISHED",
                       "categoryIds": [
                         {
                           "id": 1,
                           "name": "Technology"
                         },
                         {
                           "id": 4,
                           "name": "Science"
                         }
                       ]
                     }
    """;
        mockMvc.perform(put("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedPost))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("AI"));
    }
    @Test
    @WithMockUser
    void shouldDeletePost() throws Exception {
        Long postIdToDelete = 1L;
        mockMvc.perform(put("/api/post/delete/" + postIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}