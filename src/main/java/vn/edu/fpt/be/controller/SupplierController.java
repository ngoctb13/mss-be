package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.SupplierCreateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.SupplierUpdateDTO;
import vn.edu.fpt.be.service.SupplierService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping("/create/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierCreateDTO supplierCreateDTO, @PathVariable Long storeId) {
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierCreateDTO, storeId);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @GetMapping("/byStore/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<List<SupplierDTO>> getSuppliersByStore(@PathVariable Long storeId) {
        List<SupplierDTO> suppliers = supplierService.getSuppliersByStore(storeId);
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }
    @PostMapping("/update/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<SupplierDTO> updateSupplier(@RequestBody SupplierUpdateDTO supplierUpdateDTO, @PathVariable Long storeId) {
        SupplierDTO createdSupplier = supplierService.updateSupplier(supplierUpdateDTO, storeId);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }
    @PostMapping("/deactivate/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<SupplierDTO> deactivateSupplier(@PathVariable Long storeId) {
        SupplierDTO createdSupplier = supplierService.deactivate( storeId);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }
}
