package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;

import java.util.List;

public interface SaleInvoiceService {
    SaleInvoiceDTO createSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, double pricePaid);
    List<CustomerSaleInvoiceResponse> getSaleInvoiceByCustomer(Long customerId);
}
