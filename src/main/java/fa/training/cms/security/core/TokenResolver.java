package fa.training.cms.security.core;

import fa.training.cms.service.enums.Role;

import java.util.Map;

public interface TokenResolver {
    String generate(String username, Role role);

    Map<String, Object> verify(String token);
}
