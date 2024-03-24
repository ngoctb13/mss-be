package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.UserDTO;
import vn.edu.fpt.be.dto.request.UpdateProfileRequest;
import vn.edu.fpt.be.dto.response.UserProfileResponse;
import vn.edu.fpt.be.model.User;

public interface UserProfileService {
    boolean checkEmailExists(String email);
    UserProfileResponse getCurrentUserProfile();
    UserProfileResponse updateCurrentUserProfile(UpdateProfileRequest request);
    void changePassword(String newPassword);
}
