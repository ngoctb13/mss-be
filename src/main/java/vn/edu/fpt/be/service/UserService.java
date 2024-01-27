package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.model.User;

public interface UserService {
    String getRoleByUsername(String username);
    void registerUser(RegisterRequestDTO registerRequestDTO);
}
