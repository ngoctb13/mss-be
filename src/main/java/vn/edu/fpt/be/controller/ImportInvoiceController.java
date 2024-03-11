package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.service.ImportProductInvoiceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/import-invoice")
public class ImportInvoiceController {
    private final ImportProductInvoiceService service;

    @GetMapping("/all/by-supplier/{supplierId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getImportInvoiceByCustomer(@PathVariable Long supplierId) {
        try {
            List<SupplierImportInvoiceResponse> invoices = service.getImportInvoiceBySupplier(supplierId);
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching those import invoices!");
        }
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getSaleInvoicesByFilter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                     @RequestParam(required = false) String createdBy,
                                                     @RequestParam(required = false) Long supplierId) {
        try {
            List<ImportInvoiceReportResponse> invoices = service.getImportInvoicesByFilter(startDate, endDate, createdBy, supplierId);
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching those sale invoices!");
        }
    }
}
