package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.service.DebtPaymentHistoryService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debt-payment-history")
public class DebtPaymentHistoryController {
    private final DebtPaymentHistoryService service;

    @GetMapping("/all/by-customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getAllTransactionHistoryByCustomer(@PathVariable Long customerId) {
        try {
            List<DebtPaymentResponse> transactions = service.getAllTransactionHistoryByCustomer(customerId);
            return ResponseEntity.ok().body(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching customer's transaction!");
        }
    }

    @GetMapping("/all/by-customer/filter")
    @PreAuthorize("hasAnyAuthority('STAFF','STORE_OWNER')")
    public ResponseEntity<?> getAllTransactionHistoryByCustomerAndFilter(@RequestParam Long customerId, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<DebtPaymentResponse> transactions = service.getAllTransactionHistoryByCustomerAndDateRange(customerId, startDate, endDate);
            return ResponseEntity.ok().body(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching customer's transaction!");
        }
    }
}
