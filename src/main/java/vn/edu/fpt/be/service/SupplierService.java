package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {
    SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO);
    List<SupplierDTO> getAllSuppliers();
}
