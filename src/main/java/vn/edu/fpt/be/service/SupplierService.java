package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO, Long storeId);
    List<SupplierDTO> getAllSuppliers(int pageNumber, int pageSize);
    List<SupplierDTO> getSuppliersByStore(Long storeId, int pageNumber, int pageSize);
}
