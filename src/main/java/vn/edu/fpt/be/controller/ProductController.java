package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.service.CustomerService;
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

}
