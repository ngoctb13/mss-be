package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.dto.request.ProductWithLocationRequest;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;
import vn.edu.fpt.be.dto.response.ProductModelResponse;
import vn.edu.fpt.be.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);
    List<ProductDTO> getAllProduct();
    List<ProductDTO> getProductByStore(Long storeId, int pageNumber, int pageSize);
    ProductDTO updateProduct(ProductUpdateDTO ProductUpdateDTO, Long productId);
    ProductDTO changeStatusProduct(Long productID);
    List<ProductDTO> findProductByName(String nameInput);
    List<ProductModelResponse> findProductContainName(String nameInput);
    List<ProductModelResponse> listAllProductWithLocation(String nameInput);
    List<ProductModelResponse> listAllProduct();
    ProductModelResponse updateProductWithLocation(Long productId, ProductWithLocationRequest req);
}