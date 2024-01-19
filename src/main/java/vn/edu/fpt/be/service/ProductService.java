package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ProductBagTypeDTO;
import vn.edu.fpt.be.dto.ProductRequest;
import vn.edu.fpt.be.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product addProduct(ProductRequest productDTO);
    Product updateProduct(Long id, ProductRequest productDTO);
    Optional<Product> getProductById(Long id);
    List<ProductBagTypeDTO> getAllProductsWithBagType();
    Product deActivate(Long id);
}
