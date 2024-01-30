package vn.edu.fpt.be.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.payload.request.LoginRequest;
import vn.edu.fpt.be.payload.response.AuthResponse;
import vn.edu.fpt.be.payload.response.MessageResponse;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.TokenProvider;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String role = userService.getRoleByUsername(loginRequest.getUsername());

//        List<String> roles = userPrincipal.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .toList();


        return ResponseEntity.ok(new AuthResponse(token,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                role));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
            userService.registerUser(registerRequestDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequestDTO.getUsername(),
                        registerRequestDTO.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

//        try {
//            userService.registerUser(registrationRequest);
//
//            // Generate verification token
//            String verificationToken = emailService.generateVerificationToken();
//
//            // After registering the user, send a verification email
//            emailService.sendVerificationEmail(registrationRequest.getEmail(), verificationToken);
//
//            // Authenticate the user to generate the access token
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            registrationRequest.getEmail(),
//                            registrationRequest.getPassword()
//                    )
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            return ResponseEntity.ok("Đăng ký thành công!. Vui lòng kiểm tra email của bạn để xác nhận tài khoản của bạn.");
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
    }

}
