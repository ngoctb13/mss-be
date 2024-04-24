package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.dto.ProductUpdateDTO;
import vn.edu.fpt.be.dto.request.ProductWithLocationRequest;
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
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.ProductServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        ProductCreateDTO productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setProductName("Test Product");
        productCreateDTO.setUnit("Piece");
        productCreateDTO.setRetailPrice(10.0);
        productCreateDTO.setDescription("Test Description");
        productCreateDTO.setBag_packing("50");

        Product product = new Product();
        product.setId(1L);
        product.setProductName(productCreateDTO.getProductName());
        product.setUnit(productCreateDTO.getUnit());
        product.setRetailPrice(productCreateDTO.getRetailPrice());
        product.setDescription(productCreateDTO.getDescription());
        product.setBag_packing(productCreateDTO.getBag_packing());
        product.setStatus(Status.ACTIVE);
        product.setStore(store);
        product.setCreatedBy(user.getUsername());

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDTO result = productService.createProduct(productCreateDTO);

        // Assert
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getUnit(), result.getUnit());
        assertEquals(product.getRetailPrice(), result.getRetailPrice());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getBag_packing(), result.getBag_packing());
        assertEquals(product.getStatus(), result.getStatus());
        assertEquals(product.getStore().getId(), result.getStore().getId());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetAllProduct() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Product 1");
        product1.setStore(store);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Product 2");
        product2.setStore(store);
        products.add(product2);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreId(store.getId())).thenReturn(products);

        // Act
        List<ProductDTO> result = productService.getAllProduct();

        // Assert
        assertEquals(2, result.size());
        assertEquals(product1.getId(), result.get(0).getId());
        assertEquals(product1.getProductName(), result.get(0).getProductName());
        assertEquals(product2.getId(), result.get(1).getId());
        assertEquals(product2.getProductName(), result.get(1).getProductName());

        verify(productRepository, times(1)).findByStoreId(store.getId());
    }

    @Test
    void testGetProductByStore() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Product 1");
        product1.setStore(store);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Product 2");
        product2.setStore(store);
        products.add(product2);

        when(userService.getCurrentUser()).thenReturn(user);
        when(storeRepository.findStoreById(user.getId())).thenReturn(Optional.of(store));
        when(productRepository.findByStoreId(store.getId())).thenReturn(products);

        int pageNumber = 0;
        int pageSize = 10;

        // Act
        List<ProductDTO> result = productService.getProductByStore(store.getId(), pageNumber, pageSize);

        // Assert
        assertEquals(2, result.size());
        assertEquals(product1.getId(), result.get(0).getId());
        assertEquals(product1.getProductName(), result.get(0).getProductName());
        assertEquals(product2.getId(), result.get(1).getId());
        assertEquals(product2.getProductName(), result.get(1).getProductName());

        verify(productRepository, times(1)).findByStoreId(store.getId());
    }

    @Test
    void testUpdateProduct() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Test Product");
        product.setUnit("Piece");
        product.setRetailPrice(10.0);
        product.setDescription("Test Description");
        product.setBag_packing("50");
        product.setStatus(Status.ACTIVE);
        product.setStore(store);

        ProductUpdateDTO productUpdateDTO = new ProductUpdateDTO();
        productUpdateDTO.setProductName("Updated Product");
        productUpdateDTO.setUnit("Box");
        productUpdateDTO.setRetailPrice(20.0);
        productUpdateDTO.setDescription("Updated Description");
        productUpdateDTO.setBag_packing("kg");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setProductName(productUpdateDTO.getProductName());
        updatedProduct.setUnit(productUpdateDTO.getUnit());
        updatedProduct.setRetailPrice(productUpdateDTO.getRetailPrice());
        updatedProduct.setDescription(productUpdateDTO.getDescription());
        updatedProduct.setBag_packing(productUpdateDTO.getBag_packing());
        updatedProduct.setStatus(Status.ACTIVE);
        updatedProduct.setStore(store);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductDTO result = productService.updateProduct(productUpdateDTO, product.getId());

        // Assert
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getProductName(), result.getProductName());
        assertEquals(updatedProduct.getUnit(), result.getUnit());
        assertEquals(updatedProduct.getRetailPrice(), result.getRetailPrice());
        assertEquals(updatedProduct.getDescription(), result.getDescription());
        assertEquals(updatedProduct.getBag_packing(), result.getBag_packing());
        assertEquals(updatedProduct.getStatus(), result.getStatus());
        assertEquals(updatedProduct.getStore().getId(), result.getStore().getId());

        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testChangeStatusProduct() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Test Product");
        product.setStatus(Status.ACTIVE);

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setProductName("Test Product");
        updatedProduct.setStatus(Status.INACTIVE);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        ProductDTO result = productService.changeStatusProduct(product.getId());

        // Assert
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getProductName(), result.getProductName());
        assertEquals(updatedProduct.getStatus(), result.getStatus());

        verify(productRepository, times(1)).findById(product.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testFindProductByName() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Test Product 1");
        product1.setStore(store);
        product1.setStatus(Status.ACTIVE);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Test Product 2");
        product2.setStore(store);
        product2.setStatus(Status.ACTIVE);
        products.add(product2);

        String nameInput = "Test";

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreIdAndStatusAndProductNameContaining(store.getId(), Status.ACTIVE, nameInput))
                .thenReturn(products);

        // Act
        List<ProductDTO> result = productService.findProductByName(nameInput);

        // Assert
        assertEquals(2, result.size());
        assertEquals(product1.getId(), result.get(0).getId());
        assertEquals(product1.getProductName(), result.get(0).getProductName());
        assertEquals(product2.getId(), result.get(1).getId());
        assertEquals(product2.getProductName(), result.get(1).getProductName());

        verify(productRepository, times(1))
                .findByStoreIdAndStatusAndProductNameContaining(store.getId(), Status.ACTIVE, nameInput);
    }

    @Test
    void testFindProductContainName() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Test Product 1");
        product1.setUnit("Piece");
        product1.setRetailPrice(10.0);
        product1.setDescription("Test Description 1");
        product1.setInventory(100.00);
        product1.setBag_packing("50");
        product1.setStatus(Status.ACTIVE);
        product1.setStore(store);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Test Product 2");
        product2.setUnit("Box");
        product2.setRetailPrice(20.0);
        product2.setDescription("Test Description 2");
        product2.setInventory(100.00);
        product2.setBag_packing("50");
        product2.setStatus(Status.ACTIVE);
        product2.setStore(store);
        products.add(product2);

        List<StorageLocation> storageLocations1 = new ArrayList<>();
        StorageLocation location1 = new StorageLocation();
        location1.setId(1L);
        location1.setProduct(product1);
        storageLocations1.add(location1);

        List<StorageLocation> storageLocations2 = new ArrayList<>();
        StorageLocation location2 = new StorageLocation();
        location2.setId(2L);
        location2.setProduct(product2);
        storageLocations2.add(location2);

        String nameInput = "Test";

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreIdAndStatusAndProductNameContaining(store.getId(), Status.ACTIVE, nameInput))
                .thenReturn(products);
        when(storageLocationRepository.findByProductId(product1.getId())).thenReturn(storageLocations1);
        when(storageLocationRepository.findByProductId(product2.getId())).thenReturn(storageLocations2);

        // Act
        List<ProductModelResponse> result = productService.findProductContainName(nameInput);

        // Assert
        assertEquals(2, result.size());
        assertProductModelResponse(result.get(0), product1, storageLocations1);
        assertProductModelResponse(result.get(1), product2, storageLocations2);

        verify(productRepository, times(1))
                .findByStoreIdAndStatusAndProductNameContaining(store.getId(), Status.ACTIVE, nameInput);
        verify(storageLocationRepository, times(1)).findByProductId(product1.getId());
        verify(storageLocationRepository, times(1)).findByProductId(product2.getId());
    }
    private void assertProductModelResponse(ProductModelResponse response, Product product, List<StorageLocation> storageLocations) {
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getProductName(), response.getProductName());
        assertEquals(product.getUnit(), response.getUnit());
        assertEquals(product.getRetailPrice(), response.getRetailPrice());
        assertEquals(product.getImportPrice(), response.getImportPrice());
        assertEquals(product.getDescription(), response.getDescription());
        assertEquals(product.getInventory(), response.getInventory());
        assertEquals(product.getBag_packing(), response.getBag_packing());
        assertEquals(product.getStatus(), response.getStatus());
        assertEquals(product.getStore(), response.getStore());
        assertEquals(storageLocations, response.getStorageLocations());
    }
    @Test
    void testListAllProductWithLocation() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Test Product 1");
        product1.setUnit("Piece");
        product1.setRetailPrice(10.0);
        product1.setDescription("Test Description 1");
        product1.setInventory(100.0);
        product1.setBag_packing("50");
        product1.setStatus(Status.ACTIVE);
        product1.setStore(store);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Test Product 2");
        product2.setUnit("Box");
        product2.setRetailPrice(20.0);
        product2.setDescription("Test Description 2");
        product2.setInventory(100.0);
        product2.setBag_packing("50");
        product2.setStatus(Status.ACTIVE);
        product2.setStore(store);
        products.add(product2);

        List<StorageLocation> storageLocations1 = new ArrayList<>();
        StorageLocation location1 = new StorageLocation();
        location1.setId(1L);
        location1.setProduct(product1);
        storageLocations1.add(location1);

        List<StorageLocation> storageLocations2 = new ArrayList<>();
        StorageLocation location2 = new StorageLocation();
        location2.setId(2L);
        location2.setProduct(product2);
        storageLocations2.add(location2);

        String nameInput = "Test";

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreIdAndProductNameContaining(store.getId(), nameInput)).thenReturn(products);
        when(storageLocationRepository.findByProductId(product1.getId())).thenReturn(storageLocations1);
        when(storageLocationRepository.findByProductId(product2.getId())).thenReturn(storageLocations2);

        // Act
        List<ProductModelResponse> result = productService.listAllProductWithLocation(nameInput);

        // Assert
        assertEquals(2, result.size());
        assertProductModelResponse(result.get(0), product1, storageLocations1);
        assertProductModelResponse(result.get(1), product2, storageLocations2);

        verify(productRepository, times(1)).findByStoreIdAndProductNameContaining(store.getId(), nameInput);
        verify(storageLocationRepository, times(1)).findByProductId(product1.getId());
        verify(storageLocationRepository, times(1)).findByProductId(product2.getId());
    }

    @Test
    void testListAllProduct() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Test Product 1");
        product1.setUnit("Piece");
        product1.setRetailPrice(10.0);
        product1.setDescription("Test Description 1");
        product1.setInventory(100.0);
        product1.setBag_packing("50");
        product1.setStatus(Status.ACTIVE);
        product1.setStore(store);
        products.add(product1);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductName("Test Product 2");
        product2.setUnit("Box");
        product2.setRetailPrice(20.0);
        product2.setDescription("Test Description 2");
        product2.setInventory(100.0);
        product2.setBag_packing("50");
        product2.setStatus(Status.ACTIVE);
        product2.setStore(store);
        products.add(product2);

        List<StorageLocation> storageLocations1 = new ArrayList<>();
        StorageLocation location1 = new StorageLocation();
        location1.setId(1L);
        location1.setProduct(product1);
        storageLocations1.add(location1);

        List<StorageLocation> storageLocations2 = new ArrayList<>();
        StorageLocation location2 = new StorageLocation();
        location2.setId(2L);
        location2.setProduct(product2);
        storageLocations2.add(location2);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreId(store.getId())).thenReturn(products);
        when(storageLocationRepository.findByProductId(product1.getId())).thenReturn(storageLocations1);
        when(storageLocationRepository.findByProductId(product2.getId())).thenReturn(storageLocations2);

        // Act
        List<ProductModelResponse> result = productService.listAllProduct();

        // Assert
        assertEquals(2, result.size());
        assertProductModelResponse(result.get(0), product1, storageLocations1);
        assertProductModelResponse(result.get(1), product2, storageLocations2);

        verify(productRepository, times(1)).findByStoreId(store.getId());
        verify(storageLocationRepository, times(1)).findByProductId(product1.getId());
        verify(storageLocationRepository, times(1)).findByProductId(product2.getId());
    }

    @Test
    void testUpdateProductWithLocation() {
        // Arrange
        User user = new User();
        user.setId(1L);
        Store store = new Store();
        store.setId(1L);
        user.setStore(store);

        Product product = new Product();
        product.setId(1L);
        product.setProductName("Test Product");
        product.setUnit("Piece");
        product.setRetailPrice(10.0);
        product.setImportPrice(5.0);
        product.setDescription("Test Description");
        product.setInventory(100.00);
        product.setBag_packing("50");
        product.setStatus(Status.ACTIVE);
        product.setStore(store);

        StorageLocation location1 = new StorageLocation();
        location1.setId(1L);
        location1.setProduct(product);

        StorageLocation location2 = new StorageLocation();
        location2.setId(2L);
        location2.setProduct(null);

        List<StorageLocation> currentLocations = new ArrayList<>();
        currentLocations.add(location1);
        currentLocations.add(location2);

        ProductWithLocationRequest request = new ProductWithLocationRequest();
        request.setRetailPrice(15.0);
        request.setImportPrice(8.0);
        request.setInventory(150.0);
        request.setSelectedLocations(List.of(1L, 2L));

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setProductName("Test Product");
        updatedProduct.setUnit("Piece");
        updatedProduct.setRetailPrice(15.0);
        updatedProduct.setImportPrice(8.0);
        updatedProduct.setDescription("Test Description");
        updatedProduct.setInventory(150.0);
        updatedProduct.setBag_packing("50");
        updatedProduct.setStatus(Status.ACTIVE);
        updatedProduct.setStore(store);

        List<StorageLocation> updatedLocations = new ArrayList<>();
        StorageLocation updatedLocation1 = new StorageLocation();
        updatedLocation1.setId(1L);
        updatedLocation1.setProduct(updatedProduct);
        updatedLocations.add(updatedLocation1);
        StorageLocation updatedLocation2 = new StorageLocation();
        updatedLocation2.setId(2L);
        updatedLocation2.setProduct(updatedProduct);
        updatedLocations.add(updatedLocation2);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(storageLocationRepository.findById(1L)).thenReturn(Optional.of(location1));
        when(storageLocationRepository.findById(2L)).thenReturn(Optional.of(location2));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(storageLocationRepository.findByProductId(updatedProduct.getId())).thenReturn(updatedLocations);

        // Act
        ProductModelResponse result = productService.updateProductWithLocation(product.getId(), request);

        // Assert
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals(updatedProduct.getProductName(), result.getProductName());
        assertEquals(updatedProduct.getUnit(), result.getUnit());
        assertEquals(updatedProduct.getRetailPrice(), result.getRetailPrice());
        assertEquals(updatedProduct.getImportPrice(), result.getImportPrice());
        assertEquals(updatedProduct.getDescription(), result.getDescription());
        assertEquals(updatedProduct.getInventory(), result.getInventory());
        assertEquals(updatedProduct.getBag_packing(), result.getBag_packing());
        assertEquals(updatedProduct.getStatus(), result.getStatus());
        assertEquals(updatedProduct.getStore(), result.getStore());
        assertEquals(updatedLocations, result.getStorageLocations());

        verify(productRepository, times(1)).findById(product.getId());
        verify(storageLocationRepository, times(1)).findById(1L);
        verify(storageLocationRepository, times(1)).findById(2L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(storageLocationRepository, times(1)).findByProductId(updatedProduct.getId());
    }
}