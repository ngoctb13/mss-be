package vn.edu.fpt.be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody StoreAddDTO storeAddDTO) {
        Store store = storeService.createStore(storeAddDTO);
        return ResponseEntity.ok(store);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStore(@PathVariable Long id) {
        Store store = storeService.getStore(id);
        return ResponseEntity.ok(store);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody StoreAddDTO storeAddDTO) {
        Store updatedStore = storeService.updateStore(id, storeAddDTO);
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateStore(@PathVariable Long id) {
        storeService.deactivateStore(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }
}
