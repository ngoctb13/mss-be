package vn.edu.fpt.be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.ProductBagTypeDTO;
import vn.edu.fpt.be.dto.ProductRequest;
import vn.edu.fpt.be.model.BagType;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @PostMapping("/")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest productDTO) {
        Product product = productService.addProduct(productDTO);
        return ResponseEntity.ok(product);
    }
    @GetMapping("/")
    public ResponseEntity<List<ProductBagTypeDTO>> getAllProductsWithBagType() {
        List<ProductBagTypeDTO> productsWithBagType = productService.getAllProductsWithBagType();
        if (productsWithBagType.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productsWithBagType);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    @PutMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<Product> deactivate(@PathVariable Long id){
        Product deactivateProduct = productService.deActivate(id);
        return ResponseEntity.ok(deactivateProduct);
    }

}
