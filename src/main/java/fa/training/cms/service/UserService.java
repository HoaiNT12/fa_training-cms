package fa.training.cms.service;

import fa.training.cms.entity.User;
import fa.training.cms.service.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> findAll();
    User findByUsername(String username);
    UserDto getUserDetail(Long id);
    UserDto create(UserDto userDto);
    UserDto update(UserDto userDto);
    void inActivate(Long id);
    Optional<User> getCurrentUser();
}
