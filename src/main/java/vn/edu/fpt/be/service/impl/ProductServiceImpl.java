package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDetailDTO;
import vn.edu.fpt.be.dto.ProductUpdateSingleDTO;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;
import org.springframework.data.domain.Pageable;
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
        Product product = new Product();
        product.setProductName(productCreateDTO.getProductName());
        product.setUnit(productCreateDTO.getUnit());
        product.setRetailPrice(productCreateDTO.getRetailPrice());
        product.setWholesalePrice(productCreateDTO.getWholeSalePrice());
        product.setImportPrice(productCreateDTO.getImportPrice());
        product.setBegin_inventory(productCreateDTO.getBegin_inventory());
        product.setInventory(productCreateDTO.getInventory());
        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        Store store = ownedStores.stream()
                .filter(s -> s.getId().equals(currentUser.get().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cửa hàng không được tìm thấy với id: " + currentUser.get().getId()));
        product.setStatus(Status.ACTIVE);
        product.setStore(store);
        product.setStorageLocation(productCreateDTO.getStorageLocation());
        Product saveProduct= productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public List<ProductDTO> getAllProduct(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findAll((org.springframework.data.domain.Pageable) pageable);
        return productPage.getContent().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
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

        List<Product> products = productRepository.findByStoreId(storeId);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateSingleProduct(Long productID,boolean minusOrPlus, ProductUpdateSingleDTO productUpdateSingleDTO) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Product product = productRepository.findById(productID).orElseThrow(() -> new RuntimeException("Product not found"));
        double inventory;
        if (minusOrPlus){
            inventory = product.getInventory() + productUpdateSingleDTO.getEstimateProduct();
        }else {
            inventory = product.getInventory() - productUpdateSingleDTO.getEstimateProduct();
        }
        product.setInventory(inventory);
        Product saveProduct= productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateDetailProduct(Long productID, ProductUpdateDetailDTO productUpdateDetailDTO) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Product product = productRepository.findById(productID).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setProductName(productUpdateDetailDTO.getProductName());
        product.setUnit(productUpdateDetailDTO.getUnit());
        product.setRetailPrice(productUpdateDetailDTO.getRetailPrice());
        product.setWholesalePrice(productUpdateDetailDTO.getWholeSalePrice());
        product.setImportPrice(productUpdateDetailDTO.getImportPrice());
        product.setBegin_inventory(productUpdateDetailDTO.getBegin_inventory());
        product.setInventory(productUpdateDetailDTO.getInventory());
        product.setStorageLocation(productUpdateDetailDTO.getStorageLocation());
        Product saveProduct= productRepository.save(product);
        return modelMapper.map(saveProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deactivateProduct(Long productID) {
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
    public List<ProductDTO> findProductByName(String productName){
        List<Product> products = productRepository.findProductByProductName(productName);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

}