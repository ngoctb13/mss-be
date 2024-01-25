package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierDTO supplierDTO);
    SupplierDTO updateSupplier(SupplierDTO supplierDTO, Long supplierId);
    List<SupplierDTO> getAllSuppliers();
    List<SupplierDTO> getSuppliersByStoreId(Long storeId);
    List<SupplierDTO> getSuppliersByNameOrPhoneNumber(String searchTerm);
    SupplierDTO deactivateSupplier(Long supplierId);
}
