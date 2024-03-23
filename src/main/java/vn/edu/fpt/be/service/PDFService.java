package vn.edu.fpt.be.service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

public interface PDFService {
    ByteArrayInputStream generateInvoicePdf(Long saleInvoiceId) throws Exception;
    ByteArrayInputStream generateTransactionPdf(Long customerId, LocalDateTime startDate, LocalDateTime endDate) throws Exception;
}
