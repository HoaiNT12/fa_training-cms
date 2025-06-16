package fa.training.cms.service;

import fa.training.cms.entity.User;
import fa.training.cms.entity.Profile;
import fa.training.cms.repository.UserRepository;
import fa.training.cms.service.dto.UserDto;
import fa.training.cms.service.enums.Role;
import fa.training.cms.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private Profile profile;

    @BeforeEach
    void setUp() {
        // Create sample User and Profile with all attributes
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(Role.ADMIN);

        profile = new Profile();
        profile.setId(11L);
        profile.setFullName("Test User");
        profile.setAddress("1 Main St");
        profile.setPhoneNumber("1234567890");
        profile.setEmail("test@example.com");
        profile.setUser(user);
        user.setProfile(profile);

        userDto = new UserDto();
    }

    @Test
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        // map User -> UserDto
        doAnswer(inv -> {
            User src = inv.getArgument(0);
            UserDto dest = inv.getArgument(1);
            dest.setId(src.getId());
            dest.setUsername(src.getUsername());
            dest.setPassword(src.getPassword());
            dest.setRole(src.getRole());
            if (src.getProfile() != null) {
                dest.setFullName(src.getProfile().getFullName());
                dest.setAddress(src.getProfile().getAddress());
                dest.setPhoneNumber(src.getProfile().getPhoneNumber());
                dest.setEmail(src.getProfile().getEmail());
            }
            return null;
        }).when(modelMapper).map(any(User.class), any(UserDto.class));

        List<UserDto> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.get(0).getUsername());
        assertEquals(profile.getFullName(), result.get(0).getFullName());
        assertEquals(profile.getAddress(), result.get(0).getAddress());
        assertEquals(profile.getPhoneNumber(), result.get(0).getPhoneNumber());
        assertEquals(profile.getEmail(), result.get(0).getEmail());
    }

    @Test
    void testGetUserDetail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        doAnswer(inv -> {
            User src = inv.getArgument(0);
            UserDto dest = inv.getArgument(1);
            dest.setId(src.getId());
            dest.setUsername(src.getUsername());
            dest.setPassword(src.getPassword());
            dest.setRole(src.getRole());
            if (src.getProfile() != null) {
                dest.setFullName(src.getProfile().getFullName());
                dest.setAddress(src.getProfile().getAddress());
                dest.setPhoneNumber(src.getProfile().getPhoneNumber());
                dest.setEmail(src.getProfile().getEmail());
            }
            return null;
        }).when(modelMapper).map(any(User.class), any(UserDto.class));

        UserDto dto = userService.getUserDetail(1L);

        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(profile.getFullName(), dto.getFullName());
        assertEquals(profile.getAddress(), dto.getAddress());
        assertEquals(profile.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(profile.getEmail(), dto.getEmail());
    }

    @Test
    void testGetUserDetailNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserDetail(1L));
    }

    @Test
    void testFindByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User result = userService.findByUsername("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        User result = userService.findByUsername("testuser");
        assertNull(result);
    }

    @Test
    void testCreate() {
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(UserDto.class), eq(Profile.class))).thenReturn(profile);
        when(userRepository.save(any(User.class))).thenReturn(user);

        doAnswer(inv -> {
            User src = inv.getArgument(0);
            UserDto dest = inv.getArgument(1);
            dest.setId(src.getId());
            dest.setUsername(src.getUsername());
            dest.setPassword(src.getPassword());
            dest.setRole(src.getRole());
            if (src.getProfile() != null) {
                dest.setFullName(src.getProfile().getFullName());
                dest.setAddress(src.getProfile().getAddress());
                dest.setPhoneNumber(src.getProfile().getPhoneNumber());
                dest.setEmail(src.getProfile().getEmail());
            }
            return null;
        }).when(modelMapper).map(any(User.class), any(UserDto.class));

        UserDto inputDto = new UserDto();
        inputDto.setUsername("newUser");
        inputDto.setPassword("newPass");
        inputDto.setRole(Role.ADMIN);
        inputDto.setFullName("New User");
        inputDto.setAddress("2 Main St");
        inputDto.setPhoneNumber("5554443333");
        inputDto.setEmail("newuser@example.com");


        UserDto result = userService.create(inputDto);
        assertNotNull(result);
        // adjust assertions based on your actual logic
    }

    @Test
    void testUpdate() {
        Profile updatedProfile = new Profile(1L,"Updated User","2 Main St","5554443333","updated@example.com",null);
        User updatedUser = new User(1L,"updatedUser","updatedPass",Role.ADMIN,true,updatedProfile,null);
		updatedProfile.setUser(updatedUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(UserDto.class), eq(Profile.class))).thenReturn(profile);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);


        doAnswer(inv -> {
            User src = inv.getArgument(0);
            UserDto dest = inv.getArgument(1);
            dest.setId(src.getId());
            dest.setUsername(src.getUsername());
            dest.setPassword(src.getPassword());
            dest.setRole(src.getRole());
            if (src.getProfile() != null) {
                dest.setFullName(src.getProfile().getFullName());
                dest.setAddress(src.getProfile().getAddress());
                dest.setPhoneNumber(src.getProfile().getPhoneNumber());
                dest.setEmail(src.getProfile().getEmail());
            }
            return null;
        }).when(modelMapper).map(any(User.class), any(UserDto.class));
        UserDto inputDto = new UserDto();
        inputDto.setId(1L);
        inputDto.setUsername("updatedUser");
        inputDto.setPassword("updatedPass");
        inputDto.setRole(Role.ADMIN);
        inputDto.setFullName("Updated User");
        inputDto.setAddress("2 Main St");
        inputDto.setPhoneNumber("5554443333");
        inputDto.setEmail("updated@example.com");

        UserDto result = userService.update(inputDto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(updatedUser.getUsername(), result.getUsername());
        assertEquals(updatedUser.getPassword(), result.getPassword());
        assertEquals(updatedUser.getRole(), result.getRole());
        assertEquals(updatedProfile.getFullName(), result.getFullName());
        assertEquals(updatedProfile.getAddress(), result.getAddress());
        assertEquals(updatedProfile.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(updatedProfile.getEmail(), result.getEmail());
    }

    @Test
    void testUpdateNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto inputDto = new UserDto();
        inputDto.setId(1L);
        assertThrows(EntityNotFoundException.class, () -> userService.update(inputDto));
    }

    @Test
    void testInActivate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.inActivate(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void testInActivateNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.inActivate(1L));
    }

    @Test
    void testGetCurrentUserAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = userService.getCurrentUser();
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testGetCurrentUserNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        Optional<User> result = userService.getCurrentUser();
        assertFalse(result.isPresent());
        verifyNoInteractions(userRepository);
    }
}