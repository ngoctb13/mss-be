package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.SaleInvoiceDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.service.SaleInvoiceService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sale-invoice")
public class SaleInvoiceController {
    private final SaleInvoiceService saleInvoiceService;

    @GetMapping("/all/by-customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getSaleInvoiceByCustomer(@PathVariable Long customerId) {
        try {
            List<CustomerSaleInvoiceResponse> invoices = saleInvoiceService.getSaleInvoiceByCustomer(customerId);
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching those sale invoices!");
        }
    }

    @GetMapping("/find-by-id/{saleInvoiceId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getSaleInvoiceById(@PathVariable Long saleInvoiceId) {
        try {
            SaleInvoiceDTO invoice = saleInvoiceService.getSaleInvoiceById(saleInvoiceId);
            return ResponseEntity.ok().body(invoice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching this sale invoice!");
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getSaleInvoicesByFilter(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                     @RequestParam(required = false) String createdBy,
                                                     @RequestParam(required = false) Long customerId) {
        try {
            List<SaleInvoiceReportResponse> invoices = saleInvoiceService.getSaleInvoicesByFilter(startDate, endDate, createdBy, customerId);
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching those sale invoices!");
        }
    }

    @GetMapping("/recent-invoice")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getRecentInvoices() {
        try {
            List<SaleInvoiceReportResponse> invoices = saleInvoiceService.getRecentInvoicesByStoreId();
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching this sale invoice!");
        }
    }
    @GetMapping("/all-invoice")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getAllInvoices() {
        try {
            List<SaleInvoiceReportResponse> invoices = saleInvoiceService.getAllSaleInvoiceByCurrentStore();
            return ResponseEntity.ok().body(invoices);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching this sale invoice!");
        }
    }
}
