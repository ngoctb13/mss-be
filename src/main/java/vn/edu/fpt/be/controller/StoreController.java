package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.exception.ErrorResponse;
import vn.edu.fpt.be.service.StoreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

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
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','STORE_OWNER')")
    public ResponseEntity<?> getAllStores(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {

        try {
            List<StoreDTO> stores = storeService.getAllStores(pageNumber, pageSize);
            return new ResponseEntity<>(stores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to fetch all stores: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/owner/all")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getStoresByOwner() {
        try {
            List<StoreDTO> stores = storeService.getStoresByOwner();
            return new ResponseEntity<>(stores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse("Failed to fetch stores by owner: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
