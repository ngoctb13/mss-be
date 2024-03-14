package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.SaleInvoiceDetailDTO;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.ProductExportResponse;
import vn.edu.fpt.be.service.SaleInvoiceDetailService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sale-invoice-detail")
public class SaleInvoiceDetailController {
    private final SaleInvoiceDetailService service;

    @GetMapping("/stock-export")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getProductExportReport(
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            List<ProductExportResponse> report = service.productExportReport(customerId, startDate, endDate);
            return ResponseEntity.ok().body(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching product export report!");
        }
    }

    @GetMapping("/all/by-sale-invoice/{saleInvoiceId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getDetailsOfSaleInvoice(@PathVariable Long saleInvoiceId) {
        try {
            List<SaleInvoiceDetailDTO> details = service.getDetailsOfSaleInvoice(saleInvoiceId);
            return ResponseEntity.ok().body(details);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching details of sale invoice!");
        }
    }
}
