package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.SupplierDetailRequest;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO);
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO createDebtForSupplier(Long supplierId,SupplierDetailRequest supplierDetailRequest);
}
