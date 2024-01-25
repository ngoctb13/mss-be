package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.service.SupplierService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;
    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('STORE_OWNER', 'SYSTEM_ADMIN')")
    public List<SupplierDTO> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('STORE_OWNER', 'SYSTEM_ADMIN')")
    public List<SupplierDTO> getSuppliersByStore(@PathVariable Long storeId) {
        return supplierService.getSuppliersByStoreId(storeId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO, @RequestHeader("Authorization") String jwt) {
        User authUser = userService.findUserByJwt(jwt);
        Long storeId = supplierDTO.getStoreId();

        if (userService.isStoreOwnerOfStore(authUser.getUserId(), storeId)) {
            throw new AccessDeniedException("The specified store does not belong to the authenticated store owner.");
        }

        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        return new ResponseEntity<>(createdSupplier, HttpStatus.CREATED);
    }

    @PutMapping("/update/{supplierId}")
    @PreAuthorize("hasAnyRole('STORE_OWNER', 'SYSTEM_ADMIN')")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long supplierId, @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(supplierDTO, supplierId);
        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    @PutMapping("/deactivate/{supplierId}")
    @PreAuthorize("hasAnyRole('STORE_OWNER', 'SYSTEM_ADMIN')")
    public ResponseEntity<SupplierDTO> deactivateSupplier(@PathVariable Long supplierId) {
        SupplierDTO deactivatedSupplier = supplierService.deactivateSupplier(supplierId);
        return new ResponseEntity<>(deactivatedSupplier, HttpStatus.OK);
    }

    @GetMapping("/search/{searchTerm}")
    @PreAuthorize("hasAnyRole('STORE_OWNER', 'SYSTEM_ADMIN')")
    public ResponseEntity<List<SupplierDTO>> getCustomersByNameOrPhoneNumber(@PathVariable(required = false) String searchTerm) {
        List<SupplierDTO> suppliers = supplierService.getSuppliersByNameOrPhoneNumber(searchTerm);
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }
}
