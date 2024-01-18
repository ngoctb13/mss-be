package vn.edu.fpt.be.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.AuthenticationRequest;
import vn.edu.fpt.be.dto.AuthenticationResponse;
import vn.edu.fpt.be.dto.RegisterRequest;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        var newStoreOwner = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STORE_OWNER)
                .status(Status.ACTIVE)
                .build();
        User savedStoreOwner = userRepository.save(newStoreOwner);

        Store newStore = Store.builder()
                .storeName(request.getStoreName())
                .address(request.getStoreAddress())
                .owner(savedStoreOwner)
                .build();

        storeRepository.save(newStore);

        var jwtToken = jwtService.generateToken(newStoreOwner);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .username(request.getUsername())
                .build();
    }

}
