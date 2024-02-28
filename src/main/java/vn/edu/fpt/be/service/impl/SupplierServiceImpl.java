package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.SupplierDetailDTO;
import vn.edu.fpt.be.dto.SupplierDetailRequest;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.SupplierRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SupplierService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    public User getCurrentUser() {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        return currentUser.get();
    }
    @Override
    public SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO) {
        User currentUser = userService.getCurrentUser();

        Store store = currentUser.getStore();

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierCreateDTO.getSupplierName());
        supplier.setPhoneNumber(supplierCreateDTO.getPhoneNumber());
        supplier.setAddress(supplierCreateDTO.getAddress());
        supplier.setStatus(Status.ACTIVE);
        supplier.setStore(store);

        Supplier savedSupplier = supplierRepository.save(supplier);

        return modelMapper.map(savedSupplier, SupplierDTO.class);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        User currentUser = userService.getCurrentUser();
        List<Supplier> suppliers = supplierRepository.findByStoreId(currentUser.getStore().getId());
        return suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public SupplierDTO createDebtForSupplier(Long supplierId,SupplierDetailRequest supplierDetailRequest) {
        User currentUser = getCurrentUser();
        //
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            throw new RuntimeException("Supplier not found.");
        }
        SupplierDebtDetail inItDebtDetail = new SupplierDebtDetail();
        inItDebtDetail.setSupplier(supplier.get());
        inItDebtDetail.setCreatedBy(currentUser.getUsername());
        List<SupplierDetailDTO> supplierDetailDTOS =
    }

}
