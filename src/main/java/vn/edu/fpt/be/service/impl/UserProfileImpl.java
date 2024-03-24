package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.UserDTO;
import vn.edu.fpt.be.dto.request.UpdateProfileRequest;
import vn.edu.fpt.be.dto.response.UserProfileResponse;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.UserProfile;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.UserProfileService;
import vn.edu.fpt.be.service.UserService;

@Service
@RequiredArgsConstructor
public class UserProfileImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public boolean checkEmailExists(String email) {
        return userProfileRepository.existsByEmail(email);
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUser(currentUser);
        return modelMapper.map(userProfile, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        User currentUser = userService.getCurrentUser();
        UserProfile currentUserProfile = userProfileRepository.findByUser(currentUser);
        if (currentUserProfile == null) {
            UserProfile newUserProfile = new UserProfile();
            newUserProfile.setEmail(request.getEmail());
            newUserProfile.setFullName(request.getFullName());
            newUserProfile.setGender(request.getGender());
            newUserProfile.setDateOfBirth(request.getDateOfBirth());
            newUserProfile.setPhoneNumber(request.getPhoneNumber());
            newUserProfile.setIdentityNumber(request.getIdentityNumber());
            newUserProfile.setUser(currentUser);
            userProfileRepository.save(newUserProfile);
            return modelMapper.map(newUserProfile, UserProfileResponse.class);
        } else {
            currentUserProfile.setEmail(request.getEmail());
            currentUserProfile.setFullName(request.getFullName());
            currentUserProfile.setGender(request.getGender());
            currentUserProfile.setDateOfBirth(request.getDateOfBirth());
            currentUserProfile.setPhoneNumber(request.getPhoneNumber());
            currentUserProfile.setIdentityNumber(request.getIdentityNumber());

            UserProfile updatedProfile = userProfileRepository.save(currentUserProfile);
            return modelMapper.map(updatedProfile, UserProfileResponse.class);
        }
    }

    @Override
    public void changePassword(String newPassword) {
        User currentUser = userService.getCurrentUser();
        if (newPassword == null) {
            throw new RuntimeException("Password must not be null");
        }
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(currentUser);
        System.out.println(updatedUser);
    }
}
