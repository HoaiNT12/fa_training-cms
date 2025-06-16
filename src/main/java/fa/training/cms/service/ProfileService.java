package fa.training.cms.service;

import fa.training.cms.entity.Profile;

public interface ProfileService {
    Profile findById(Long id);
    Profile create(Profile profile);
    Profile update(Profile profile);
}
