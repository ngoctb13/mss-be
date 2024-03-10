package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SupplierImportInvoiceResponse;
import vn.edu.fpt.be.service.ImportProductInvoiceService;

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
}
