package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.UserProfile;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.UserProfileRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getCurrentUser() {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        return currentUser.get();
    }
    @Override
    public String getRoleByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return String.valueOf(userOptional.get().getRole());
    }

    @Override
    public void registerUser(RegisterRequestDTO registerRequestDTO) {
        String username = registerRequestDTO.getUsername();
        if (username.matches("^\\d.*")) {
            throw new IllegalArgumentException("Username cannot begin with a number.");
        }

        Optional<User> existingUser = userRepository.findByUsername(registerRequestDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException(registerRequestDTO.getUsername() + " dã tồn tại! Hãy bấm quên mật khẩu nếu bạn không nhớ mật khẩu của mình!");
        }

        User user = modelMapper.map(registerRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRole(Role.STORE_OWNER);
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public UserProfileDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        UserProfile userProfile = userProfileRepository.findByUser(user.get());
        if (userProfile  == null) {
            userProfile = new UserProfile();
            userProfile.setUser(user.get());
//            userProfile.setCreatedAt(LocalDateTime.now());
        }
        userProfile.setFullName(userUpdateDTO.getFullName());
        userProfile.setGender(userUpdateDTO.getGender());
        userProfile.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        userProfile.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        userProfile.setIdentityNumber(userUpdateDTO.getIdentityNumber());
//        user.get().setUpdatedAt(new Date());
        userRepository.save(user.get());
        userProfileRepository.save(userProfile);

        return modelMapper.map(userProfile, UserProfileDTO.class);
    }

    @Override
    public UserDTO createStaffAccount(StaffCreateDTO staffCreateDTO) {
        Optional<User> existingUser = userRepository.findByUsername(staffCreateDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException(staffCreateDTO.getUsername() + " dã tồn tại! Hãy bấm quên mật khẩu nếu bạn không nhớ mật khẩu của mình!");
        }

        User currentUser = getCurrentUser();
        String prefixStaffAccount = String.valueOf(currentUser.getStore().getId()) + "/";
        User newStaff = new User();
        newStaff.setUsername(prefixStaffAccount + staffCreateDTO.getUsername());
        newStaff.setPassword(passwordEncoder.encode(staffCreateDTO.getPassword()));
        newStaff.setRole(Role.STAFF);
        newStaff.setStore(currentUser.getStore());
        newStaff.setStatus(Status.ACTIVE);
        newStaff.setCreatedBy(currentUser.getUsername());

        User savedStaff = userRepository.save(newStaff);
        return modelMapper.map(savedStaff, UserDTO.class);
    }
}
