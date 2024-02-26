package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;

import java.util.List;

public interface SaleInvoiceDetailService {
    List<SaleInvoiceDetailDTO> createSaleInvoiceDetail(List<SaleInvoiceDetailRequest> request);
}
