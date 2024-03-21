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
import vn.edu.fpt.be.dto.UserProfileDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.payload.request.LoginRequest;
import vn.edu.fpt.be.payload.response.AuthResponse;
import vn.edu.fpt.be.payload.response.MessageResponse;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.TokenProvider;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ForgotPasswordService;
import vn.edu.fpt.be.service.UserProfileService;
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
    private final UserProfileService userProfileService;
    private final ForgotPasswordService forgotPasswordService;
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
        String status = userService.getStatusByUsername(loginRequest.getUsername());

        return ResponseEntity.ok(new AuthResponse(token,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                role,
                status));
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
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            forgotPasswordService.requestForgotPassword(email);
            return ResponseEntity.ok("hihi");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Consider a more informative error structure
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            if (forgotPasswordService.checkIsUsed(token)) {
                return ResponseEntity.badRequest().body("Token của bạn đã được dùng!");
            } else if (forgotPasswordService.isExpired(token)) {
                return ResponseEntity.badRequest().body("Token của bạn đã hết hạn! Vui lòng thử lại.");
            } else {
                forgotPasswordService.resetPassword(token, newPassword);
                return ResponseEntity.ok("Đặt lại mật khẩu thành công! Hãy đăng nhập lại.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @GetMapping("/check-token-valid")
    public ResponseEntity<?> checkTokenValid(@RequestParam("token") String token) {
        boolean isExpired = forgotPasswordService.isExpired(token);
        boolean isUsed = forgotPasswordService.checkIsUsed(token);

        if (isExpired) {
            return ResponseEntity.badRequest().body("Token đã hết hạn");
        } else if (isUsed) {
            return ResponseEntity.badRequest().body("Token đã được sử dụng");
        } else {
            return ResponseEntity.ok().body("Token hợp lệ");
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        boolean exists = userService.checkUsernameExists(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = userProfileService.checkEmailExists(email);
        return ResponseEntity.ok(exists);
    }
}
