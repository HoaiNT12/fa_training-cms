package fa.training.cms.web.rest;

import fa.training.cms.entity.Profile;
import fa.training.cms.entity.User;
import fa.training.cms.service.ProfileService;
import fa.training.cms.service.UserService;
import fa.training.cms.service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserResource {
    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserDetail(@PathVariable Long id){
        UserDto userDto = userService.getUserDetail(id);
        if(userDto == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDto);
    }

	@PostMapping()
    public ResponseEntity<UserDto> insert(@RequestBody UserDto userDto){
		UserDto user = userService.create(userDto);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping()
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.update(userDto));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        try {
            userService.inActivate(id); // Calls the corresponding service method
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e)
        {
            return  ResponseEntity.notFound().build();
        }
    }
}
