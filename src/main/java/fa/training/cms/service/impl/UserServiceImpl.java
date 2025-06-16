package fa.training.cms.service.impl;

import fa.training.cms.entity.Profile;
import fa.training.cms.entity.User;
import fa.training.cms.repository.UserRepository;
import fa.training.cms.service.UserService;
import fa.training.cms.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
	private final ModelMapper modelMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
		List<UserDto> userDtos = users.stream()
                .map(user ->{
                    Profile profile = user.getProfile();
                    UserDto userDto = new UserDto();
                    modelMapper.map(user, userDto);
                    if (profile != null) {
                        modelMapper.map(profile, userDto);
                    }
                    return userDto;
                }).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    @Transactional
    public UserDto getUserDetail(Long userId) {
		User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
		Profile profile = user.getProfile();
		UserDto userDetailsDto = new UserDto();
        modelMapper.map(user, userDetailsDto);
        if (profile != null) {
            modelMapper.map(profile, userDetailsDto);
        }
        return userDetailsDto;
    }

    @Override
    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        Profile profile = modelMapper.map(userDto, Profile.class);
        user.setProfile(profile);
        profile.setUser(user);
        User insertedUser = userRepository.save(user);
        modelMapper.map(insertedUser, userDto);
        modelMapper.map(profile, userDto);
        return userDto;
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto) throws EntityNotFoundException{
        Optional<User> existingUser = userRepository.findById(userDto.getId());
        if(!existingUser.isPresent()){
            throw new EntityNotFoundException("User not found");
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(existingUser.get().getPassword());
        Profile profile = modelMapper.map(userDto, Profile.class);
        user.setProfile(profile);
        profile.setUser(user);
        User updatedUser = userRepository.save(user);
        modelMapper.map(updatedUser, userDto);
        modelMapper.map(updatedUser.getProfile(), userDto);
        return userDto;
    }

    @Override
    public void inActivate(Long id) throws EntityNotFoundException {
		Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setActive(false);
            userRepository.save(user);
        }
        else{
            throw new EntityNotFoundException("User not found with id "+ id);
        }
    }

    @Transactional
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }
}
