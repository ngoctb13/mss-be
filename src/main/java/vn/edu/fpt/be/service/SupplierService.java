package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.SupplierUpdateRequest;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplierService {
    //hihi
    SupplierDTO createSupplier(SupplierCreateDTO supplierCreateDTO);
    List<SupplierDTO> getAllSuppliers();
    SupplierDTO deactivate(Long supplierId);
    SupplierDTO updateSupplier(SupplierUpdateRequest request, Long supplierId);
    List<SupplierDTO> getSuppliersTotalDebtGreaterThan(double totalDebt);
}
