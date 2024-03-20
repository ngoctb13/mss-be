package vn.edu.fpt.be.service;

import java.io.ByteArrayInputStream;

public interface PDFService {
    ByteArrayInputStream generateInvoicePdf(Long saleInvoiceId) throws Exception;
}
