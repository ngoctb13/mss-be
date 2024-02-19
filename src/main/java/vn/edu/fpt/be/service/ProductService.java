package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);
    List<ProductDTO> getAllProduct();
    List<ProductDTO> getProductByStore(Long storeId);
}
