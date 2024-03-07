package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.service.SaleInvoiceService;

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
}
