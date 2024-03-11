package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.response.ProductExportResponse;
import vn.edu.fpt.be.model.SaleInvoice;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleInvoiceDetailService {
    void saveSaleInvoiceDetail(List<SaleInvoiceDetailRequest> request, SaleInvoice invoice);
    List<ProductExportResponse> productExportReport(Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
