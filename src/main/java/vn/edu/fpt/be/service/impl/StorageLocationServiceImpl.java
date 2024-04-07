package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.ProductService;
import vn.edu.fpt.be.service.StorageLocationService;
import vn.edu.fpt.be.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {
    private final StorageLocationRepository repo;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public StorageLocationDTO createStorageLocation(StorageLocationRequest storageLocationRequest) {
        User currentUser = userService.getCurrentUser();
        Store ownedStore = currentUser.getStore();
        if (ownedStore == null) {
            throw new RuntimeException("Store cannot be null");
        }
        StorageLocation newStorageLocation = new StorageLocation();
        newStorageLocation.setLocationName(storageLocationRequest.getLocationName());
        newStorageLocation.setDescription(storageLocationRequest.getDescription());
        newStorageLocation.setStatus(Status.ACTIVE);
        newStorageLocation.setStore(ownedStore);
        newStorageLocation.setCreatedBy(currentUser.getUsername());
        StorageLocation savedStorageLocation = repo.save(newStorageLocation);
        return modelMapper.map(savedStorageLocation, StorageLocationDTO.class);
    }

    @Override
    public StorageLocationDTO updateStorageLocation(StorageLocationRequest storageLocationRequest, Long storageLocationId) {
        User currentUser = userService.getCurrentUser();
        StorageLocation existingStorageLocation = repo.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Storage location not found"));
        // Retrieve the store based on the provided storeId
        Store store = currentUser.getStore();

        existingStorageLocation.setLocationName(storageLocationRequest.getLocationName());
        existingStorageLocation.setDescription(storageLocationRequest.getDescription());
        existingStorageLocation.setStatus(Status.ACTIVE);
        existingStorageLocation.setStore(store);

        StorageLocation updatedStorageLocation = repo.save(existingStorageLocation);
        return modelMapper.map(updatedStorageLocation, StorageLocationDTO.class);
    }

    @Override
    public List<StorageLocationDTO> getByStore() {
        User currentUser = userService.getCurrentUser();
        Store currentStore = currentUser.getStore();
        List<StorageLocation> storageLocations = repo.findByStoreId(currentStore.getId());
        return storageLocations.stream()
                .map(storageLocation -> modelMapper.map(storageLocation, StorageLocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StorageLocationDTO deactivate(Long storageLocationId) {
        StorageLocation existingStorageLocation = repo.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Storage location not found"));
        if (existingStorageLocation.getStatus() == Status.INACTIVE) {
            existingStorageLocation.setStatus(Status.ACTIVE);
        } else {
            existingStorageLocation.setStatus(Status.INACTIVE);
        }
        StorageLocation deactivatedStorageLocation = repo.save(existingStorageLocation);
        return modelMapper.map(deactivatedStorageLocation, StorageLocationDTO.class);
    }


    @Override
    public ProductLocationResponse listProductLocation() {
        User currentUser = userService.getCurrentUser();
        List<Product> products = productRepository.findByStoreId(currentUser.getStore().getId()); // Lấy tất cả sản phẩm

        List<ProductLocationResponse.ProductWithLocations> productWithLocationsList = products.stream().map(product -> {
            List<StorageLocation> locations = repo.findByProductId(product.getId()); // Tìm vị trí của từng sản phẩm
            List<ProductLocationResponse.LocationInfo> locationInfos = locations.stream()
                    .map(location -> new ProductLocationResponse.LocationInfo(location.getId(), location.getLocationName(), location.getDescription()))
                    .collect(Collectors.toList());

            return new ProductLocationResponse.ProductWithLocations(
                    product.getId(),
                    product.getProductName(),
                    product.getUnit(), // Thêm unit
                    product.getRetailPrice(), // Thêm retailPrice
                    product.getInventory(), // Thêm inventory
                    product.getBag_packing(), // Thêm bag_packing
                    product.getStatus(),
                    locationInfos // Thêm thông tin vị trí
            );
        }).collect(Collectors.toList());

        return new ProductLocationResponse(productWithLocationsList);
    }

    @Override
    public List<StorageLocationDTO> addNewStorageLocation(StorageLocationForProductRequest storageLocationRequest) {
        User currentUser = userService.getCurrentUser();
        Store ownedStore = currentUser.getStore();
        if (ownedStore == null) {
            throw new RuntimeException("Store cannot be null");
        }

        Product existsProduct = productRepository.findById(storageLocationRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        List<StorageLocationDTO> savedLocations = new ArrayList<>();
        for (Long storageId : storageLocationRequest.getStorageLocationIds()) {
            StorageLocation existsStorageLocation = repo.findById(storageId)
                    .orElseThrow(() -> new IllegalArgumentException("Storage location not found: " + storageId));

            // Tạo một đối tượng StorageLocation mới và thiết lập các thuộc tính
            StorageLocation newStorageLocation = new StorageLocation();
            newStorageLocation.setLocationName(existsStorageLocation.getLocationName());
            newStorageLocation.setDescription(existsStorageLocation.getDescription());
            newStorageLocation.setStatus(Status.ACTIVE);
            newStorageLocation.setStore(ownedStore);
            newStorageLocation.setCreatedBy(currentUser.getUsername());
            newStorageLocation.setProduct(existsProduct);

            // Lưu StorageLocation mới
            StorageLocation savedStorageLocation = repo.save(newStorageLocation);
            savedLocations.add(modelMapper.map(savedStorageLocation, StorageLocationDTO.class));
        }

        return savedLocations;
    }



}