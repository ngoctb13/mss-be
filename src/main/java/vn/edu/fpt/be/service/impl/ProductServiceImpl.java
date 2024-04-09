package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.dto.SupplierDTO;
import vn.edu.fpt.be.dto.request.ProductWithLocationRequest;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;
import vn.edu.fpt.be.dto.response.ProductModelResponse;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.StorageLocation;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.ProductRepository;
import vn.edu.fpt.be.repository.StorageLocationRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;
import vn.edu.fpt.be.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    private ProductDTO convertToDto(ProductDTO productDTO) {
        return modelMapper.map(productDTO, ProductDTO.class);
    }
    @Override
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        User currentUser = userService.getCurrentUser();
        Product product = new Product();

        product.setUnit(productCreateDTO.getUnit());
        product.setRetailPrice(productCreateDTO.getRetailPrice());

        product.setDescription(productCreateDTO.getDescription());
        product.setBag_packing(productCreateDTO.getBag_packing());
        product.setProductName(productCreateDTO.getProductName());
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
            List<Product> products = productRepository.findByStoreIdAndStatusAndProductNameContaining(currentStore.getId(), Status.ACTIVE, nameInput);

            return products.stream()
                    .map(product -> modelMapper.map(product, ProductDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving product: ", e);
        }
    }

    @Override
    public List<ProductModelResponse> findProductContainName(String nameInput) {
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        List<Product> products = productRepository.findByStoreIdAndStatusAndProductNameContaining(currentStore.getId(), Status.ACTIVE, nameInput);

        List<ProductModelResponse> productModelResponses = new ArrayList<>();

        for (Product product : products) {
            List<StorageLocation> storageLocations = storageLocationRepository.findByProductId(product.getId());

            ProductModelResponse response = ProductModelResponse.builder()
                    .id(product.getId())
                    .productName(product.getProductName())
                    .unit(product.getUnit())
                    .retailPrice(product.getRetailPrice())
                    .importPrice(product.getImportPrice())
                    .description(product.getDescription())
                    .inventory(product.getInventory())
                    .bag_packing(product.getBag_packing())
                    .status(product.getStatus())
                    .store(product.getStore())
                    .storageLocations(storageLocations)
                    .build();

            productModelResponses.add(response);
        }

        return productModelResponses;
    }

    @Override
    public List<ProductModelResponse> listAllProductWithLocation(String nameInput) {
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        List<Product> products = productRepository.findByStoreIdAndProductNameContaining(currentStore.getId(), nameInput);

        List<ProductModelResponse> productModelResponses = new ArrayList<>();

        for (Product product : products) {
            List<StorageLocation> storageLocations = storageLocationRepository.findByProductId(product.getId());

            ProductModelResponse response = ProductModelResponse.builder()
                    .id(product.getId())
                    .productName(product.getProductName())
                    .unit(product.getUnit())
                    .retailPrice(product.getRetailPrice())
                    .importPrice(product.getImportPrice())
                    .description(product.getDescription())
                    .inventory(product.getInventory())
                    .bag_packing(product.getBag_packing())
                    .status(product.getStatus())
                    .store(product.getStore())
                    .storageLocations(storageLocations)
                    .build();

            productModelResponses.add(response);
        }

        return productModelResponses;
    }

    @Override
    public List<ProductModelResponse> listAllProduct() {
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        List<Product> products = productRepository.findByStoreId(currentStore.getId());

        List<ProductModelResponse> productModelResponses = new ArrayList<>();

        for (Product product : products) {
            List<StorageLocation> storageLocations = storageLocationRepository.findByProductId(product.getId());

            ProductModelResponse response = ProductModelResponse.builder()
                    .id(product.getId())
                    .productName(product.getProductName())
                    .unit(product.getUnit())
                    .retailPrice(product.getRetailPrice())
                    .importPrice(product.getImportPrice())
                    .description(product.getDescription())
                    .inventory(product.getInventory())
                    .bag_packing(product.getBag_packing())
                    .status(product.getStatus())
                    .store(product.getStore())
                    .storageLocations(storageLocations)
                    .build();

            productModelResponses.add(response);
        }

        return productModelResponses;
    }

    @Override
    @Transactional
    public ProductModelResponse updateProductWithLocation(Long productId, ProductWithLocationRequest req) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();

            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                throw new IllegalArgumentException("Can not found any product with id " + productId);
            }
            if (!product.get().getStore().equals(currentStore)) {
                throw new IllegalArgumentException("This product not belong to current store");
            }

            product.get().setRetailPrice(req.getRetailPrice());
            product.get().setImportPrice(req.getImportPrice());
            product.get().setInventory(req.getInventory());

            List<StorageLocation> currentLocations = storageLocationRepository.findByProductId(productId);
            for (Long locationId : req.getSelectedLocations()) {
                Optional<StorageLocation> locationOptional = storageLocationRepository.findById(locationId);
                if (locationOptional.isPresent()) {
                    StorageLocation location = locationOptional.get();
                    if (location.getProduct() == null || !location.getProduct().equals(product.get())) {
                        location.setProduct(product.get());
                        storageLocationRepository.save(location);
                    }
                }
            }

            // Kiểm tra và cập nhật trường product của storage locations không nằm trong selectedLocations
            for (StorageLocation location : currentLocations) {
                if (!req.getSelectedLocations().contains(location.getId())) {
                    location.setProduct(null);
                    storageLocationRepository.save(location);
                }
            }

            Product updatedProduct = productRepository.save(product.get());
            ProductModelResponse response = new ProductModelResponse();
            response.setId(updatedProduct.getId());
            response.setProductName(updatedProduct.getProductName());
            response.setUnit(updatedProduct.getUnit());
            response.setRetailPrice(updatedProduct.getRetailPrice());
            response.setImportPrice(updatedProduct.getImportPrice());
            response.setDescription(updatedProduct.getDescription());
            response.setInventory(updatedProduct.getInventory());
            response.setBag_packing(updatedProduct.getBag_packing());
            response.setStatus(updatedProduct.getStatus());
            response.setStore(updatedProduct.getStore());

            List<StorageLocation> updatedLocations = storageLocationRepository.findByProductId(updatedProduct.getId());
            response.setStorageLocations(updatedLocations);

            return response;

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity violation", e);
        } catch (DataAccessException e) {
            throw new RuntimeException("Data access exception occurred", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred", e);
        }
    }
}
