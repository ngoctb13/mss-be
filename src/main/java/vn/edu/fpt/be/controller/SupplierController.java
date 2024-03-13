package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.service.SupplierService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createSupplier(@RequestBody SupplierCreateDTO supplierCreateDTO) {
        try {
            SupplierDTO createdSupplier = supplierService.createSupplier(supplierCreateDTO);
            return ResponseEntity.ok().body(createdSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the supplier.");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER','STAFF')")
    public ResponseEntity<?> getAllSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
            return ResponseEntity.ok().body(suppliers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the supplier.");
        }
    }

    @PutMapping("/deactivate/{supplierId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> deactivate(@PathVariable Long supplierId) {
        try {
            SupplierDTO deactivateSupplier = supplierService.deactivate(supplierId);
            return ResponseEntity.ok().body(deactivateSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deactivate the supplier.");
        }
    }

    @PutMapping("/update/{supplierId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> updateSupplier(@RequestBody SupplierUpdateRequest request, @PathVariable Long supplierId) {
        try {
            SupplierDTO updatedSupplier = supplierService.updateSupplier(request,supplierId);
            return ResponseEntity.ok().body(updatedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the supplier.");
        }
    }

    @GetMapping("/all/supplier-have-debt")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getAllSuppliersHaveDebt() {
        try {
            double totalDebt = 0.0;
            List<SupplierDTO> suppliers = supplierService.getSuppliersTotalDebtGreaterThan(totalDebt);
            return ResponseEntity.status(HttpStatus.OK).body(suppliers);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
