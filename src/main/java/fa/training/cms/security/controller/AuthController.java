package fa.training.cms.security.controller;

import fa.training.cms.entity.User;
import fa.training.cms.security.core.TokenResolver;
import fa.training.cms.security.dto.LoginDto;
import fa.training.cms.service.UserService;
import fa.training.cms.service.dto.UserDto;
import fa.training.cms.service.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final TokenResolver tokenResolver;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, TokenResolver tokenResolver, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenResolver = tokenResolver;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> authenticate(@RequestBody LoginDto loginDto) {
        User user = userService.findByUsername(loginDto.getUsername());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String token = tokenResolver.generate(user.getUsername(), user.getRole());
        return Map.of("token", token);
    }

    @GetMapping("/seed")
    @Transactional
    public void seed() {
        UserDto userAdmin = new UserDto();
        userAdmin.setUsername("admin");
        userAdmin.setPassword(passwordEncoder.encode("123456"));
        userAdmin.setRole(Role.ADMIN);
        userAdmin.setEmail("admin@gmail.com");
        userAdmin.setAddress("FPT tower");
        userAdmin.setFullName("admin");
        userAdmin.setPhoneNumber("1234567890");
        userService.create(userAdmin);

        UserDto userEditor = new UserDto();
        userEditor.setUsername("editor");
        userEditor.setPassword(passwordEncoder.encode("123457"));
        userEditor.setRole(Role.EDITOR);
        userEditor.setEmail("editor@gmail.com");
        userEditor.setAddress("FPT tower");
        userEditor.setFullName("editor");
        userEditor.setPhoneNumber("0123456789");
        userService.create(userEditor);
    }
}
