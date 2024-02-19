package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private ProductDTO convertToDto(ProductDTO productDTO) {
        return modelMapper.map(productDTO, ProductDTO.class);
    }
    @Override
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Optional<Product> existingProduct = productRepository.findByProductName(productCreateDTO.getProductName());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Sản phẩm "+ productCreateDTO.getProductName() + " dã tồn tại! Hãy nhập mặt hàng có tên khác");
        }
        Product product = new Product();
        product.setProductName(productCreateDTO.getProductName());
        product.setUnit(productCreateDTO.getUnit());
        product.setRetailPrice(productCreateDTO.getRetailPrice());
        product.setWholesalePrice(productCreateDTO.getWholeSalePrice());
        product.setImportPrice(productCreateDTO.getImportPrice());
        product.setInventory(productCreateDTO.getInventory());
        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        Store store = ownedStores.stream()
                .filter(s -> s.getId().equals(currentUser.get().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cửa hàng không được tìm thấy với id: " + currentUser.get().getId()));
        product.setStore(store);
        productRepository.save(product);
        return null;
    }

    @Override
    public List<ProductDTO> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(customer -> modelMapper.map(products, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductByStore(Long storeId) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }

        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(storeId));
        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + storeId + " is not owned by the current user.");
        }

        List<Product> products = productRepository.findByProductByStoreId(storeId);
        return products.stream()
                .map(customer -> modelMapper.map(products, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
