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
import vn.edu.fpt.be.dto.request.PersonalDebtCreateRequest;
import vn.edu.fpt.be.dto.request.ProductWithLocationRequest;
import vn.edu.fpt.be.dto.response.PersonalDebtResponse;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;
import vn.edu.fpt.be.dto.response.ProductModelResponse;
import vn.edu.fpt.be.exception.ErrorResponse;
import vn.edu.fpt.be.model.Product;
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
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> getAllCustomers() {
        try {
            List<ProductDTO> products = productService.getAllProduct();
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{storeId}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
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
    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> updateProduct(@RequestBody ProductUpdateDTO productUpdateDTO, @PathVariable Long productId) {
        try {
            ProductDTO updateProduct = productService.updateProduct(productUpdateDTO, productId);
            return ResponseEntity.status(HttpStatus.OK).body(updateProduct);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/by-name/{nameInput}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<List<ProductDTO>> findProductByName(
            @PathVariable String nameInput) {
        try {
            List<ProductDTO> products = productService.findProductByName(nameInput);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/contain-name/{nameInput}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> findProductContainName(
            @PathVariable String nameInput) {
        try {
            List<ProductModelResponse> products = productService.findProductContainName(nameInput);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/change-status/{productId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
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

    @GetMapping("/all/with-location/{nameInput}")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> listAllProductWithLocation(
            @PathVariable String nameInput) {
        try {
            List<ProductModelResponse> products = productService.listAllProductWithLocation(nameInput);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/list-all")
    @PreAuthorize("hasAnyAuthority('STORE_OWNER', 'STAFF')")
    public ResponseEntity<?> listAll() {
        try {
            List<ProductModelResponse> products = productService.listAllProduct();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update-with-location/{productId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> updateProductWithLocation(@PathVariable Long productId, @RequestBody ProductWithLocationRequest request) {
        try {
            ProductModelResponse res = productService.updateProductWithLocation(productId, request);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
