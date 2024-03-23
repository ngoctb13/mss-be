package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.service.PDFService;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pdf")
public class PdfController {

    private final PDFService pdfService;

    @GetMapping("/sale-invoice/{invoiceId}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<InputStreamResource> downloadInvoicePdf(@PathVariable Long invoiceId) throws Exception {
        ByteArrayInputStream bis = pdfService.generateInvoicePdf(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/transactions/by-filter")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<InputStreamResource> downloadTransactionPdf(@RequestParam Long customerId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) throws Exception {
        ByteArrayInputStream bis = pdfService.generateTransactionPdf(customerId, startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=transactions.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
