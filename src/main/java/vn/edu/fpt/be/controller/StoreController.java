package vn.edu.fpt.be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.dto.StoreUpdateDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.service.StoreService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<StoreAddDTO> createStore(@RequestBody StoreAddDTO storeAddDTO, @RequestHeader("Authorization") String jwt) {
        User authUser = userService.findUserByJwt(jwt);
        storeAddDTO.setOwner(authUser);
        if (userService.isUserIsStoreOwner(authUser.getUserId())) {
            throw new AccessDeniedException("The specified store does not belong to the authenticated store owner.");
        }
        StoreAddDTO createdStore = storeService.createStore(storeAddDTO);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<StoreUpdateDTO> updateStore(@PathVariable Long storeId, @RequestBody StoreUpdateDTO storeUpdateDTO) {
        StoreUpdateDTO updatedStore = storeService.updateStore(storeId, storeUpdateDTO);
        return new ResponseEntity<>(updatedStore, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }
    @PutMapping("/deactivate/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Store> deactivateStore(@PathVariable Long storeId) {
        Store deactivateStore = storeService.deactivateStore(storeId);
        return new ResponseEntity<>(deactivateStore, HttpStatus.OK);
    }
}
