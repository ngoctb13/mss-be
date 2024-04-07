package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.request.DebtRecordRequest;
import vn.edu.fpt.be.dto.request.PaymentRecordRequest;
import vn.edu.fpt.be.dto.response.DebtRecordResponse;
import vn.edu.fpt.be.dto.response.PaymentRecordResponse;
import vn.edu.fpt.be.model.enums.SourceType;
import vn.edu.fpt.be.service.DebtRecordService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debt-record")
public class DebtRecordController {
    private final DebtRecordService service;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createDebtRecord(@RequestBody DebtRecordRequest request) {
        try {
            Long sourceId = null;
            DebtRecordResponse response = service.createDebtRecord(request, SourceType.CUSTOMER_DEBT, null);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while create debt record!");
        }
    }

    @GetMapping("/find-by-id/{debtRecordId}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER','STAFF')")
    public ResponseEntity<?> getDebtRecordById(@PathVariable Long debtRecordId) {
        try {
            DebtRecordResponse response = service.getDebtRecordById(debtRecordId);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching debt record!");
        }
    }
}
