package vn.edu.fpt.be.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.*;
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
    public SupplierDTO deactivate(Long supplierId) {
        Supplier deactivatedSupplier = supplierRepository.findById(supplierId).orElseThrow(() -> new RuntimeException("Supplier not found"));
        if (deactivatedSupplier.getStatus()==Status.ACTIVE){
            deactivatedSupplier.setStatus(Status.INACTIVE);
        }else {
            deactivatedSupplier.setStatus(Status.ACTIVE);
        }
        Supplier savedSupplier= supplierRepository.save(deactivatedSupplier);
        return modelMapper.map(savedSupplier, SupplierDTO.class);
    }

    @Override
    public SupplierDTO updateSupplier(SupplierUpdateRequest request, Long supplierId) {
        User currentUser = userService.getCurrentUser();
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (supplier.isEmpty()) {
            throw new RuntimeException("Supplier not found!");
        }
        Supplier currentSupplier = supplier.get();
        currentSupplier.setSupplierName(request.getSupplierName());
        currentSupplier.setPhoneNumber(request.getPhoneNumber());
        currentSupplier.setAddress(request.getAddress());
        currentSupplier.setNote(request.getNote());
        Supplier updatedSupplier= supplierRepository.save(currentSupplier);
        return modelMapper.map(updatedSupplier, SupplierDTO.class);
    }
}
