package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.service.UserProfileService;

@Service
@RequiredArgsConstructor
public class UserProfileImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    @Override
    public boolean checkEmailExists(String email) {
        return userProfileRepository.existsByEmail(email);
    }
}
