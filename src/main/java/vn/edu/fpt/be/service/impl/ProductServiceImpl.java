package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    private ProductDTO convertToDto(ProductDTO productDTO) {
        return modelMapper.map(productDTO, ProductDTO.class);
    }
    @Override
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        User currentUser = userService.getCurrentUser();
        Product product = new Product();
        product.setProductName(productCreateDTO.getProductName());
        product.setUnit(productCreateDTO.getUnit());
        product.setRetailPrice(productCreateDTO.getRetailPrice());

        product.setDescription(productCreateDTO.getDescription());
        product.setBag_packing(productCreateDTO.getBag_packing());
        Store ownStore = currentUser.getStore();
        product.setStatus(Status.ACTIVE);
        product.setStore(ownStore);
        product.setCreatedBy(currentUser.getUsername());
        Product saveProduct= productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAllProduct() {
        User currentUser = userService.getCurrentUser();
        List<Product> productPage = productRepository.findByStoreId(currentUser.getStore().getId());
        return productPage.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductByStore(Long storeId, int pageNumber, int pageSize) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }

        Optional<Store> ownedStores = storeRepository.findStoreById(currentUser.get().getId());
        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(storeId));
        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + storeId + " is not owned by the current user.");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products = productRepository.findByStoreId(storeId);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(ProductUpdateDTO productUpdateDTO, Long productId) {
        User currentUser = userService.getCurrentUser();
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found!");
        }
        Product currentProduct = product.get();
        currentProduct.setProductName(productUpdateDTO.getProductName());
        currentProduct.setUnit(productUpdateDTO.getUnit());
        currentProduct.setRetailPrice(productUpdateDTO.getRetailPrice());
        currentProduct.setDescription(productUpdateDTO.getDescription());
        currentProduct.setBag_packing(productUpdateDTO.getBag_packing());
        Product updatedProduct= productRepository.save(currentProduct);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO changeStatusProduct(Long productID) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Product product = productRepository.findById(productID).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStatus()==Status.ACTIVE){
            product.setStatus(Status.INACTIVE);
        }else {
            product.setStatus(Status.ACTIVE);
        }
        Product saveProduct= productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public List<ProductDTO> findProductByName(String nameInput) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            List<Product> products = productRepository.findByStoreIdAndProductNameContaining(currentStore.getId(), nameInput);

            return products.stream()
                    .map(product -> modelMapper.map(product, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving product: ", e);
        }
    }
}
