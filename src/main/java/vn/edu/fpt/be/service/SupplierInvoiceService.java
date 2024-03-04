package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.SupplierDebtDetailRequest;
import vn.edu.fpt.be.dto.SupplierDebtInvoiceDTO;

import java.util.List;

public interface SupplierInvoiceService {
    SupplierDebtInvoiceDTO createSaleInvoice(Long supplierId, List<SupplierDebtDetailRequest> requests, double pricePaid);
}
