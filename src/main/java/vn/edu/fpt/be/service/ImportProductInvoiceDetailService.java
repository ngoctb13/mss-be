package vn.edu.fpt.be.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.edu.fpt.be.dto.ImportProductDetailRequest;
import vn.edu.fpt.be.dto.ImportProductDetailResponse;
import vn.edu.fpt.be.model.ImportProductInvoice;

import java.util.List;

public interface ImportProductInvoiceDetailService {
    void saveImportProductInvoiceDetail(List<ImportProductDetailRequest> request, ImportProductInvoice invoice);
}
