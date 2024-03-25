package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.dto.StorageLocationRequest;
import vn.edu.fpt.be.dto.request.StorageLocationForProductRequest;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;
import vn.edu.fpt.be.service.StorageLocationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storage-locations")
public class StorageLocationController {
    private final StorageLocationService service;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createStorageLocation(@RequestBody StorageLocationRequest request) {
        try {
            StorageLocationDTO createdStorageLocation = service.createStorageLocation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStorageLocation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the storage location.");
        }
    }

    @PutMapping("/update/{storageLocationId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> updateStorageLocation(@RequestBody StorageLocationRequest storageLocationRequest,
                                                                    @PathVariable Long storageLocationId) {
        try {
            StorageLocationDTO updatedStorageLocation = service.updateStorageLocation(storageLocationRequest, storageLocationId);
            return ResponseEntity.status(HttpStatus.OK).body(updatedStorageLocation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the customer.");
        }
    }

    @GetMapping("/by-store")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER','STAFF')")
    public ResponseEntity<?> getStorageLocationsByStore() {
        try {
            List<StorageLocationDTO> storageLocations = service.getByStore();
            if (storageLocations.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(storageLocations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the storage locations.");
        }
    }

    @PutMapping("/deactivate/{storageLocationId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> deactivateStorageLocation(@PathVariable Long storageLocationId) {
        try {
            StorageLocationDTO deactivatedStorageLocation = service.deactivate(storageLocationId);
            return ResponseEntity.status(HttpStatus.OK).body(deactivatedStorageLocation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deactivating the bag type.");
        }
    }
    @GetMapping("/products/locations")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<ProductLocationResponse> getAllProductsWithLocations() {
        try {
            ProductLocationResponse response = service.listProductLocation(); // Giả sử bạn đã cập nhật service để có phương thức này
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ProductLocationResponse()); // Trả về một response rỗng hoặc thông báo lỗi tùy bạn
        }
    }
    @PostMapping("/add-location-for-product")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<List<StorageLocationDTO>> addNewStorageLocations(@RequestBody StorageLocationForProductRequest request) {
        try {
            List<StorageLocationDTO> newStorageLocations = service.addNewStorageLocation(request);
            return ResponseEntity.ok(newStorageLocations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
