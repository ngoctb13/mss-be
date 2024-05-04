package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductInvoiceResponse;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.model.ImportProductInvoice;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportProductInvoiceService {
    ImportProductInvoiceResponse importProduct(Long customerId, List<ImportProductDetailRequest> listProductDetails, Double pricePaid);
    List<SupplierImportInvoiceResponse> getImportInvoiceByCustomer(Long customerId);
    List<ImportInvoiceReportResponse> getImportInvoicesByFilter(LocalDateTime startDate, LocalDateTime endDate, String createdBy, Long customerId);
    ImportProductInvoiceResponse getImportInvoiceById(Long importInvoiceId);
}
