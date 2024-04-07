package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.UserDTO;
import vn.edu.fpt.be.dto.request.UpdateProfileRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.UserProfileResponse;
import vn.edu.fpt.be.payload.response.MessageResponse;
import vn.edu.fpt.be.service.UserProfileService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/current-user")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER','SYSTEM_ADMIN')")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            UserProfileResponse response = userProfileService.getCurrentUserProfile();
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching current user profile!");
        }
    }
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER','SYSTEM_ADMIN')")
    public ResponseEntity<?> getUserProfileByUser(@PathVariable Long userId) {
        try {
            UserProfileResponse response = userProfileService.getUserProfileByUser(userId);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching user profile!");
        }
    }
    @PostMapping("/current-user/update")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER','SYSTEM_ADMIN')")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UpdateProfileRequest req) {
        try {
            UserProfileResponse response = userProfileService.updateCurrentUserProfile(req);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching current user profile!");
        }
    }

    @PostMapping("/current-user/change-password")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER','SYSTEM_ADMIN')")
    public ResponseEntity<?> changeCurrentUserPassword(@RequestParam("newPassword") String newPassword) {
        try {
            userProfileService.changePassword(newPassword);
            return ResponseEntity.ok(new MessageResponse("change password successfully!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing current user password!");
        }
    }
}
