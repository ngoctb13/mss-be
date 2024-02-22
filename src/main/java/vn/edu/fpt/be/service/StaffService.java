package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.dto.StaffCreateDTO;
import vn.edu.fpt.be.dto.StaffDTO;

import java.util.List;

public interface StaffService {
    void createStaff(StaffCreateDTO staffCreateDTO);
    List<StaffDTO> getAllStaffs(int pageNumber, int pageSize);
    List<StaffDTO> getStaffsByStore(Long storeId);
}
