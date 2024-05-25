package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.response.ImportInvoiceDetailResponse;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.service.ImportProductInvoiceDetailService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import-invoice-detail")
public class ImportInvoiceDetailController {
    private final ImportProductInvoiceDetailService service;
    @GetMapping("/find-by-import-invoice/{importInvoiceId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getSaleInvoicesByFilter(@PathVariable Long importInvoiceId){
        try {
            List<ImportInvoiceDetailResponse> details = service.getDetailsOfImportInvoice(importInvoiceId);
            return ResponseEntity.ok().body(details);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching those import invoices!");
        }
    }
}
