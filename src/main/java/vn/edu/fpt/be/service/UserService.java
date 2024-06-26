package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.model.User;

import java.util.List;

public interface UserService {
    String getRoleByUsername(String username);
    void registerUser(RegisterRequestDTO registerRequestDTO);
    UserProfileDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO);
    UserDTO createStaffAccount(StaffCreateDTO staffCreateDTO);
    List<UserDTO> getAllStaffOfStore();
    List<UserDTO> getUserOfStore();
    UserDTO getUserById(Long storeId);
    boolean checkUsernameExists(String username);
    User getCurrentUser();
    List<User> getAllUser();
    User deactivateUser(Long id);
    String getStatusByUsername(String username);
    UserDTO changePasswordByUserId(Long userId, String newPassword);
}
