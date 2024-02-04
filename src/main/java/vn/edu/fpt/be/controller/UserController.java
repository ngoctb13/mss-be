package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.UserProfileDTO;
import vn.edu.fpt.be.dto.UserUpdateDTO;
import vn.edu.fpt.be.security.CurrentUser;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> updateCurrentUser(@CurrentUser UserPrincipal userPrincipal, @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            UserProfileDTO updatedUser = userService.updateUser(userPrincipal.getId(), userUpdateDTO);
            return ResponseEntity.ok().body(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the staff member.");
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
}
