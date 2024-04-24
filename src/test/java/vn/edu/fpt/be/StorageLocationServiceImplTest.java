package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.dto.StorageLocationRequest;
import vn.edu.fpt.be.dto.request.StorageLocationForProductRequest;
import vn.edu.fpt.be.dto.response.ProductLocationResponse;
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
import vn.edu.fpt.be.service.impl.StorageLocationServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StorageLocationServiceImplTest {

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StorageLocationServiceImpl storageLocationService;

    private User user;
    private Store store;
    private Product product;
    private StorageLocation storageLocation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        store = new Store();
        user.setStore(store);
        product = new Product();
        storageLocation = new StorageLocation();
        storageLocation.setId(1L);
        storageLocation.setLocationName("Test Location");
        storageLocation.setDescription("Test Description");
        storageLocation.setStatus(Status.ACTIVE);
        storageLocation.setStore(store);
        storageLocation.setProduct(product);
    }

    @Test
    void testCreateStorageLocation() {
        StorageLocationRequest request = new StorageLocationRequest();
        request.setLocationName("New Location");
        request.setDescription("New Description");

        when(userService.getCurrentUser()).thenReturn(user);
        when(storageLocationRepository.save(any(StorageLocation.class))).thenReturn(storageLocation);

        StorageLocationDTO result = storageLocationService.createStorageLocation(request);

        assertEquals(storageLocation.getLocationName(), result.getLocationName());
        assertEquals(storageLocation.getDescription(), result.getDescription());
        assertEquals(storageLocation.getStatus(), result.getStatus());
    }

    @Test
    void testUpdateStorageLocation() {
        StorageLocationRequest request = new StorageLocationRequest();
        request.setLocationName("Updated Location");
        request.setDescription("Updated Description");

        when(userService.getCurrentUser()).thenReturn(user);
        when(storageLocationRepository.findById(1L)).thenReturn(Optional.of(storageLocation));
        when(storageLocationRepository.save(any(StorageLocation.class))).thenReturn(storageLocation);

        StorageLocationDTO result = storageLocationService.updateStorageLocation(request, 1L);

        assertEquals(request.getLocationName(), result.getLocationName());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(Status.ACTIVE, result.getStatus());
    }

    @Test
    void testGetByStore() {
        List<StorageLocation> storageLocations = Arrays.asList(storageLocation);

        when(userService.getCurrentUser()).thenReturn(user);
        when(storageLocationRepository.findByStoreId(store.getId())).thenReturn(storageLocations);

        List<StorageLocationDTO> result = storageLocationService.getByStore();

        assertEquals(1, result.size());
        assertEquals(storageLocation.getLocationName(), result.get(0).getLocationName());
    }

    @Test
    void testDeactivate() {
        when(storageLocationRepository.findById(1L)).thenReturn(Optional.of(storageLocation));
        when(storageLocationRepository.save(any(StorageLocation.class))).thenReturn(storageLocation);

        StorageLocationDTO result = storageLocationService.deactivate(1L);

        assertEquals(Status.INACTIVE, result.getStatus());
    }

    @Test
    void testListProductLocation() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductName("Product 1");
        product1.setUnit("Unit 1");
        product1.setRetailPrice(10.0);
        product1.setInventory(100.0);
        product1.setBag_packing("50");
        product1.setStatus(Status.ACTIVE);
        product1.setStore(store);

        StorageLocation location1 = new StorageLocation();
        location1.setId(1L);
        location1.setLocationName("Location 1");
        location1.setDescription("Description 1");
        location1.setProduct(product1);

        StorageLocation location2 = new StorageLocation();
        location2.setId(2L);
        location2.setLocationName("Location 2");
        location2.setDescription("Description 2");
        location2.setProduct(product1);

        List<Product> products = Arrays.asList(product1);
        List<StorageLocation> locations = Arrays.asList(location1, location2);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findByStoreId(store.getId())).thenReturn(products);
        when(storageLocationRepository.findByProductId(product1.getId())).thenReturn(locations);

        ProductLocationResponse result = storageLocationService.listProductLocation();

        assertEquals(1, result.getProducts().size());
        ProductLocationResponse.ProductWithLocations productWithLocations = result.getProducts().get(0);
        assertEquals(product1.getId(), productWithLocations.getProductId());
        assertEquals(product1.getProductName(), productWithLocations.getProductName());
        assertEquals(product1.getUnit(), productWithLocations.getUnit());
        assertEquals(product1.getRetailPrice(), productWithLocations.getRetailPrice());
        assertEquals(product1.getInventory(), productWithLocations.getInventory());
        assertEquals(product1.getBag_packing(), productWithLocations.getBag_packing());
        assertEquals(product1.getStatus(), productWithLocations.getStatus());
        assertEquals(2, productWithLocations.getLocations().size());
    }

    @Test
    void testAddNewStorageLocation() {
        Long productId = 1L;
        Long storageLocationId1 = 1L;
        Long storageLocationId2 = 2L;

        Product product = new Product();
        product.setId(productId);

        StorageLocation storageLocation1 = new StorageLocation();
        storageLocation1.setId(storageLocationId1);
        storageLocation1.setLocationName("Location 1");
        storageLocation1.setDescription("Description 1");

        StorageLocation storageLocation2 = new StorageLocation();
        storageLocation2.setId(storageLocationId2);
        storageLocation2.setLocationName("Location 2");
        storageLocation2.setDescription("Description 2");

        StorageLocationForProductRequest request = new StorageLocationForProductRequest();
        request.setProductId(productId);
        request.setStorageLocationIds(Arrays.asList(storageLocationId1, storageLocationId2));

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(storageLocationRepository.findById(storageLocationId1)).thenReturn(Optional.of(storageLocation1));
        when(storageLocationRepository.findById(storageLocationId2)).thenReturn(Optional.of(storageLocation2));
        when(storageLocationRepository.save(any(StorageLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<StorageLocationDTO> result = storageLocationService.addNewStorageLocation(request);

        assertEquals(2, result.size());
        assertEquals(storageLocation1.getLocationName(), result.get(0).getLocationName());
        assertEquals(storageLocation1.getDescription(), result.get(0).getDescription());
        assertEquals(storageLocation2.getLocationName(), result.get(1).getLocationName());
        assertEquals(storageLocation2.getDescription(), result.get(1).getDescription());
    }

    @Test
    void testAddNewStorageLocationWithNonExistingProduct() {
        Long productId = 1L;
        Long storageLocationId = 1L;

        StorageLocationForProductRequest request = new StorageLocationForProductRequest();
        request.setProductId(productId);
        request.setStorageLocationIds(Arrays.asList(storageLocationId));

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> storageLocationService.addNewStorageLocation(request));
    }

    @Test
    void testAddNewStorageLocationWithNonExistingStorageLocation() {
        Long productId = 1L;
        Long storageLocationId = 1L;

        Product product = new Product();
        product.setId(productId);

        StorageLocationForProductRequest request = new StorageLocationForProductRequest();
        request.setProductId(productId);
        request.setStorageLocationIds(Arrays.asList(storageLocationId));

        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(storageLocationRepository.findById(storageLocationId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> storageLocationService.addNewStorageLocation(request));
    }
}