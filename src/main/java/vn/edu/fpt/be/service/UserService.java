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
    UserDTO getUserById(Long storeId);
    User getCurrentUser();
}
