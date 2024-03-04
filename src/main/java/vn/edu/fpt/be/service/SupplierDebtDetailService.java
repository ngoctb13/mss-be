package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SupplierDebtDetailDTO;
import vn.edu.fpt.be.dto.SupplierDebtDetailRequest;
import vn.edu.fpt.be.model.SupplierDebtDetail;

import java.util.List;

public interface SupplierDebtDetailService {
    List<SupplierDebtDetailDTO> createDebtDetail(List<SupplierDebtDetailRequest> supplierDebtDetailRequests);
}
