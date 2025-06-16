package fa.training.cms.service.impl;

import fa.training.cms.entity.Profile;
import fa.training.cms.repository.ProfileRepository;
import fa.training.cms.service.ProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Profile findById(Long id) {
        Optional<Profile> profile = profileRepository.findById(id);
        return profile.orElse(null);
    }

    @Override
    public Profile create(Profile profile) {
        return profileRepository.save(profile);
    }

    @Override
    public Profile update(Profile profile) throws EntityNotFoundException{
        if(profile.getId() == null || !profileRepository.existsById(profile.getId())){
            throw new EntityNotFoundException("Profile not found");
        }
        return profileRepository.save(profile);
    }
}
