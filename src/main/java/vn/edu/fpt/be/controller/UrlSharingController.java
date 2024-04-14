package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.response.CustomerInformation;
import vn.edu.fpt.be.dto.response.DebtPaymentResponse;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;
import vn.edu.fpt.be.service.UrlSharingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/url-sharing")
public class UrlSharingController {

    private final UrlSharingService urlSharingService;

    @GetMapping("/list-transactions")
    public ResponseEntity<?> getAllTransactionHistoryByUrlSharingToken(@RequestParam("sharingToken") String sharingToken) {
        try {
            List<DebtPaymentResponse> transactionHistory = urlSharingService.getAllTransactionHistoryByUrlSharingToken(sharingToken);
            return new ResponseEntity<>(transactionHistory, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customer-infor")
    public ResponseEntity<?> getCustomerInformation(@RequestParam("sharingToken") String sharingToken) {
        try {
            CustomerInformation information = urlSharingService.getCustomerInformation(sharingToken);
            return new ResponseEntity<>(information, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/check-token-valid")
    public ResponseEntity<?> checkTokenValid(@RequestParam("token") String token) {
        boolean isExpired = urlSharingService.isExpired(token);

        if (isExpired) {
            return ResponseEntity.badRequest().body("Token đã hết hạn");
        } else {
            return ResponseEntity.ok().body("Token hợp lệ");
        }
    }
}
