package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.ImportInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.service.PaymentRecordService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-record")
public class PaymentRecordController {
    private final PaymentRecordService service;

    @PostMapping("/pay-debt")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> payDebt(@RequestBody PaymentRecordRequest request) {
        try {
            PaymentRecordResponse response = service.createPaymentRecord(request);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while pay debt!");
        }
    }
}
