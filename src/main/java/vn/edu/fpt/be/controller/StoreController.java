package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.service.StoreService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<?> createStore(@RequestBody StoreCreateDTO storeCreateDTO) {
        try {
            return ResponseEntity.ok(storeService.createStore(storeCreateDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
