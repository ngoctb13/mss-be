package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;

import java.util.List;

public interface SaleInvoiceService {
    SaleInvoiceDTO createSaleInvoice(Long customerId, List<SaleInvoiceDetailRequest> requests, double pricePaid);

}
