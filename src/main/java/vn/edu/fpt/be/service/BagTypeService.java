package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.BagTypeRequest;
import vn.edu.fpt.be.dto.BagTypeDTO;
import vn.edu.fpt.be.model.BagType;

import java.util.List;

public interface BagTypeService {
    BagTypeDTO createBagType(BagTypeRequest bagTypeRequest);
    BagTypeDTO updateBagType(BagTypeRequest bagTypeRequest, Long bagTypeId);
    BagTypeDTO deactivate(Long bagTypeId);
    List<BagTypeDTO> getAllBagTypes();

}
