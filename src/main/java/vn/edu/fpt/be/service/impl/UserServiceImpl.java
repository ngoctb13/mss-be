package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;
    @Override
    public String getRoleByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return String.valueOf(userOptional.get().getRole());
    }

    @Override
    public void registerUser(RegisterRequestDTO registerRequestDTO) {
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
}
