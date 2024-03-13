package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.*;
import vn.edu.fpt.be.dto.response.StoreResponse;
import vn.edu.fpt.be.exception.ErrorResponse;
import vn.edu.fpt.be.service.ImportProductInvoiceService;
import vn.edu.fpt.be.service.SaleInvoiceService;
import vn.edu.fpt.be.service.StoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;
    private final ImportProductInvoiceService importProductInvoiceService;
    private final SaleInvoiceService saleInvoiceService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createStore(@RequestBody StoreCreateDTO storeCreateDTO) {
        try {
            return ResponseEntity.ok(storeService.createStore(storeCreateDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<?> getAllStores() {
        try {
            List<StoreDTO> stores = storeService.getAllStores();
            return new ResponseEntity<>(stores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to fetch all stores: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/by-owner/{ownerId}")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<?> getStoresByOwner(@PathVariable Long ownerId) {
        try {
            StoreDTO store = storeService.getStoreByOwner(ownerId);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to fetch store by owner: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import-invoice")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createImportInvoice(@RequestBody CreateImportProductInvoiceRequest request) {
        try {
            ImportProductInvoiceResponse response = importProductInvoiceService.importProduct(
                    request.getSupplierId(),
                    request.getProductDetails(),
                    request.getPricePaid()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // You might want to handle different exceptions differently
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PostMapping("/create-sale-invoice")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createSaleInvoice(@RequestBody CreateSaleInvoiceRequest request) {
        try {
            SaleInvoiceDTO response = saleInvoiceService.createSaleInvoice(
                    request.getCustomerId(),
                    request.getProductDetails(),
                    request.getPricePaid()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // You might want to handle different exceptions differently
            return ResponseEntity.badRequest().body(e);
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getStoresByFilter(@RequestParam(required = false) String storeName,
                                               @RequestParam(required = false) String address,
                                               @RequestParam(required = false) String phoneNumber,
                                               @RequestParam(required = false) String status) {
        try {
            List<StoreResponse> stores = storeService.getStoreByFilter(storeName, address, phoneNumber, status);
            return new ResponseEntity<>(stores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to fetch stores: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
