package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDetailDTO;
import vn.edu.fpt.be.dto.ProductUpdateSingleDTO;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);
    List<ProductDTO> getAllProduct(int pageNumber, int pageSize);
    List<ProductDTO> getProductByStore(Long storeId);
    ProductDTO updateSingleProduct(Long productID,boolean minusOrPlus, ProductUpdateSingleDTO productUpdateSingleDTO);
    ProductDTO updateDetailProduct(Long productID, ProductUpdateDetailDTO productUpdateDetailDTO);
    ProductDTO deactivateProduct(Long productID);
    List<ProductDTO> findProductByName(String ProductName);

}