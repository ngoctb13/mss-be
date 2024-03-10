package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductInvoiceResponse;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.model.ImportProductInvoice;

import java.util.List;

public interface ImportProductInvoiceService {
    ImportProductInvoiceResponse importProduct(Long supplierId, List<ImportProductDetailRequest> listProductDetails, Double pricePaid);
    List<SupplierImportInvoiceResponse> getImportInvoiceBySupplier(Long customerId);
}
