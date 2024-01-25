package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Supplier;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.SupplierRepository;
import vn.edu.fpt.be.service.SupplierService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final StoreRepository storeRepository;

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierDTO.getSupplierName());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setNote(supplierDTO.getNote());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        supplier.setStore(storeRepository.findById(supplierDTO.getStoreId()).get());
        supplier.setStatus(Status.ACTIVE);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return modelMapper.map(savedSupplier, SupplierDTO.class);
    }

    @Override
    public SupplierDTO updateSupplier(SupplierDTO supplierDTO, Long supplierId) {
        Optional<Supplier> existingSupplierOptional = supplierRepository.findById(supplierId);

        if (existingSupplierOptional.isPresent()) {
            Supplier existingSupplier = existingSupplierOptional.get();

            existingSupplier.setSupplierName(supplierDTO.getSupplierName());
            existingSupplier.setPhoneNumber(supplierDTO.getPhoneNumber());
            existingSupplier.setAddress(supplierDTO.getAddress());
            existingSupplier.setNote(supplierDTO.getNote());

            Supplier updatedSupplier = supplierRepository.save(existingSupplier);
            return modelMapper.map(updatedSupplier, SupplierDTO.class);
        } else {
            throw new RuntimeException("Supplier not found with ID: " + supplierId);
        }
    }

    @Override
    public List<SupplierDTO> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierDTO> getSuppliersByStoreId(Long storeId) {
        List<Supplier> suppliers = supplierRepository.findByStore_StoreId(storeId);
        return suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierDTO> getSuppliersByNameOrPhoneNumber(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                List<Supplier> allSuppliers = supplierRepository.findAll();
                return allSuppliers.stream()
                        .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                        .collect(Collectors.toList());
            }
            List<Supplier> suppliers = supplierRepository.findBySupplierNameOrPhoneNumberContaining(searchTerm);
            return suppliers.stream()
                    .map(supplier -> modelMapper.map(supplier, SupplierDTO.class))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("An error occurred while retrieving customers. Please try again later.");
        }
    }

    @Override
    public SupplierDTO deactivateSupplier(Long supplierId) {
        Optional<Supplier> existingSupplierOptional = supplierRepository.findById(supplierId);

        if (existingSupplierOptional.isPresent()) {
            Supplier existingSupplier = existingSupplierOptional.get();
            existingSupplier.setStatus(Status.INACTIVE); // Assuming you want to deactivate the supplier
            Supplier deactivatedSupplier = supplierRepository.save(existingSupplier);
            return modelMapper.map(deactivatedSupplier, SupplierDTO.class);
        } else {
            // Handle the case where the supplier with the given ID doesn't exist
            throw new RuntimeException("Supplier not found with ID: " + supplierId);
        }
    }
}
