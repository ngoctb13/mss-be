package vn.edu.fpt.be.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.Supplier;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.SupplierRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.SupplierService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
    @Override
    public SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO, Long storeId) {

        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }

        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(storeId));
        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + storeId + " is not owned by the current user.");
        }

        // Retrieve the store based on the provided storeId
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierCreateDTO.getSupplierName());
        supplier.setPhoneNumber(supplierCreateDTO.getPhoneNumber());
        supplier.setAddress(supplierCreateDTO.getAddress());
        supplier.setNote(supplierCreateDTO.getNote());
        supplier.setStatus(Status.ACTIVE);
        supplier.setStore(store);

        Supplier savedSupplier = supplierRepository.save(supplier);

        return modelMapper.map(savedSupplier, SupplierDTO.class);
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierDTO> getSuppliersByStore(Long storeId) {
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }

        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(storeId));
        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + storeId + " is not owned by the current user.");
        }

        List<Supplier> suppliers = supplierRepository.findByStoreId(storeId);
        return suppliers.stream()
                .map(supplier -> modelMapper.map(suppliers, SupplierDTO.class))
                .collect(Collectors.toList());
    }
}
