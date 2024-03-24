package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StaffCreateDTO;
import vn.edu.fpt.be.dto.UserDTO;
import vn.edu.fpt.be.dto.UserProfileDTO;
import vn.edu.fpt.be.dto.UserUpdateDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.security.CurrentUser;
import vn.edu.fpt.be.security.TokenProvider;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/me")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> updateCurrentUser(@CurrentUser UserPrincipal userPrincipal, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserProfileDTO updatedUser = userService.updateUser(userPrincipal.getId(), userUpdateDTO);
            return ResponseEntity.ok().body(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while update your account.");
        }
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')") // Adjust the authority as needed
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserProfileDTO updatedUserProfile = userService.updateUser(userId, userUpdateDTO);
            return ResponseEntity.ok(updatedUserProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Consider a more informative error structure
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @PostMapping("/createStaff")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createStaff(@RequestBody StaffCreateDTO staffCreateDTO) {
        try {
            UserDTO newStaff = userService.createStaffAccount(staffCreateDTO);
            return ResponseEntity.ok(newStaff);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Consider a more informative error structure
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build(); // General error handling, consider logging or a more specific error message
        }
    }

    @GetMapping("/getUserById/{userId}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER','STAFF','SYSTEM_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
//            Long userId = tokenProvider.getUserIdFromToken(jwt);
            UserDTO currentUser = userService.getUserById(userId);
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all-staff")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getUserById() {
        try {
//            Long userId = tokenProvider.getUserIdFromToken(jwt);
            List<UserDTO> listStaff = userService.getAllStaffOfStore();
            return ResponseEntity.ok(listStaff);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all/by-store")
    @PreAuthorize("hasAnyAuthority('STAFF', 'STORE_OWNER')")
    public ResponseEntity<?> getUsersByStore() {
        try {
            List<UserDTO> users = userService.getUserOfStore();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/all-user")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<?> getAllUser() {
        try {
            List<User> users = userService.getAllUser();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/deactivate/{userId}")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'STORE_OWNER')")
    public ResponseEntity<?> deactivate(@PathVariable Long userId){
        try{
            User deactivateUser = userService.deactivateUser(userId);
            return ResponseEntity.ok().body(deactivateUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deactivate the supplier.");
        }
    }
}
