package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.dto.StaffCreateDTO;

public interface StaffService {
    void createStaff(StaffCreateDTO staffCreateDTO);
}
