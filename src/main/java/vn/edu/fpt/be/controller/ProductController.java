package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.exception.ErrorResponse;
import vn.edu.fpt.be.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        try {
            ProductDTO createdProduct = productService.createProduct(productCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the product.");
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getAllCustomers(@RequestParam(defaultValue = "0") int pageNumber,
                                             @RequestParam(defaultValue = "5") int pageSize) {
        try {
            List<ProductDTO> products = productService.getAllProduct(pageNumber, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getProductsByStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize) {
        try {
            List<ProductDTO> products = productService.getProductByStore(storeId, pageNumber, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO) {
        try {
            ProductDTO updateProduct = productService.updateProduct(productUpdateDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> findProductByName(
            @RequestParam("productName") String productName) {
        try {
            List<ProductDTO> products = productService.findProductByName(productName);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PutMapping("/change-status/{productId}")
    public ResponseEntity<ProductDTO> changeStatusProduct(@PathVariable Long productId) {
        try {
            ProductDTO updatedProduct = productService.changeStatusProduct(productId);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}