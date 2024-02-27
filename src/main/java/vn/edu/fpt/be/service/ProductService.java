package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.model.Product;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);
    List<ProductDTO> getAllProduct(int pageNumber, int pageSize);
    List<ProductDTO> getProductByStore(Long storeId, int pageNumber, int pageSize);
    ProductDTO updateProduct(ProductUpdateDTO ProductUpdateDTO);
    ProductDTO changeStatusProduct(Long productID);
    List<ProductDTO> findProductByName(String productName);
}