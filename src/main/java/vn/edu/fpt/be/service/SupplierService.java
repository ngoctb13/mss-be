package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.SupplierUpdateDTO;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO, Long storeId);
    List<SupplierDTO> getAllSuppliers();
    List<SupplierDTO> getSuppliersByStore(Long storeId);
    SupplierDTO updateSupplier(SupplierUpdateDTO supplierUpdateDTO, Long supplierId);
    SupplierDTO deactivate(Long supplierId);
}
