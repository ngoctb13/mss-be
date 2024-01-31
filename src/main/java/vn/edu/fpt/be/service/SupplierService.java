package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierDTO supplierDTO);
    SupplierDTO getSupplierById(Long id);
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);
    void deleteSupplier(Long id);
}
