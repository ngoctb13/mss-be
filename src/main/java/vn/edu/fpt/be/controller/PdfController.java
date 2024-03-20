package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.be.service.PDFService;

import java.io.ByteArrayInputStream;

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
}
