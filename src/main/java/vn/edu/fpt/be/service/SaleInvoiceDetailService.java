package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.SaleInvoiceDetailRequest;
import vn.edu.fpt.be.model.ImportProductInvoice;
import vn.edu.fpt.be.model.SaleInvoice;

import java.util.List;

public interface SaleInvoiceDetailService {
    void saveSaleInvoiceDetail(List<SaleInvoiceDetailRequest> request, SaleInvoice invoice);
}
