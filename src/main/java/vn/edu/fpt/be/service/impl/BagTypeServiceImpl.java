package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.BagTypeDTO;
import vn.edu.fpt.be.dto.BagTypeRequest;
import vn.edu.fpt.be.model.BagType;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.BagTypeRepository;
import vn.edu.fpt.be.service.BagTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BagTypeServiceImpl implements BagTypeService {
    private final BagTypeRepository bagTypeRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public BagTypeDTO createBagType(BagTypeRequest bagTypeRequest) {
        BagType bagType = modelMapper.map(bagTypeRequest, BagType.class);
        BagType savedBagType = bagTypeRepository.save(bagType);
        return modelMapper.map(savedBagType, BagTypeDTO.class);
    }

    @Override
    public BagTypeDTO updateBagType(BagTypeRequest bagTypeRequest, Long bagTypeId) {
        BagType existingBagType = bagTypeRepository.findById(bagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Bag type not found"));
        modelMapper.map(bagTypeRequest, existingBagType);
        BagType updatedBagType = bagTypeRepository.save(existingBagType);
        return modelMapper.map(updatedBagType, BagTypeDTO.class);
    }

    @Override
    public BagTypeDTO deactivate(Long bagTypeId) {
        BagType existingBagType = bagTypeRepository.findById(bagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Bag type not found"));
        if (existingBagType.getStatus()==Status.ACTIVE){
            existingBagType.setStatus(Status.INACTIVE);
        }else{
            existingBagType.setStatus(Status.ACTIVE);
        }

        BagType deactivatedBagType = bagTypeRepository.save(existingBagType);
        return modelMapper.map(deactivatedBagType, BagTypeDTO.class);
    }

    @Override
    public List<BagTypeDTO> getAllBagTypes() {
        List<BagType> bagTypes = bagTypeRepository.findAll();
        return bagTypes.stream()
                .map(bagType -> modelMapper.map(bagType, BagTypeDTO.class))
                .collect(Collectors.toList());
    }
}
