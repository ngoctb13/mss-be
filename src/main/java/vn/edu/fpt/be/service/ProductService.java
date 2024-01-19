package vn.edu.fpt.be.service;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.fpt.be.dto.ProductBagTypeDTO;
import vn.edu.fpt.be.dto.ProductRequest;
import vn.edu.fpt.be.model.BagType;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private Product getProduct(ProductRequest productDTO, Product product) {
        product.setDescription(productDTO.getDescription());
        product.setProductName(productDTO.getProduct_name());
        product.setUnit(productDTO.getUnit());
        product.setRetailPrice(productDTO.getRetail_price());
        product.setImportPrice(product.getImportPrice());
        product.setWholesalePrice(product.getWholesalePrice());
        return productRepository.save(product);
    }
    @Autowired
    private ProductRepository productRepository;
    public Product addProduct(ProductRequest productDTO) {
        Product product = new Product();

        return getProduct(productDTO, product);
    }

    public Product updateProduct(Long id, ProductRequest productDTO) {
        Product product = productRepository.
                findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return getProduct(productDTO, product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<ProductBagTypeDTO> getAllProductsWithBagType() {
        List<Object[]> results = productRepository.findAllInformationOfProduct();
        return results.stream()
                .map(result -> new ProductBagTypeDTO((Product) result[0], (BagType) result[1]))
                .collect(Collectors.toList());
    }

    public Product deActivate(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException(("Product not found")));
        product.setStatus(Status.INACTIVE);
        return productRepository.save(product);
    }
    public void test() {
        //okok
        //TuyetVoi
        //ditconem
    }
}
