package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleInvoiceService {
    SaleInvoiceDTO createSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, double pricePaid);
    List<CustomerSaleInvoiceResponse> getSaleInvoiceByCustomer(Long customerId);
    List<SaleInvoiceReportResponse> getSaleInvoicesByFilter(LocalDateTime startDate, LocalDateTime endDate, String createdBy, Long customerId);
    SaleInvoiceDTO getSaleInvoiceById(Long saleInvoiceId);
    List<SaleInvoiceReportResponse> getRecentInvoicesByStoreId();
    List<SaleInvoiceReportResponse> getAllSaleInvoiceByCurrentStore();
}
