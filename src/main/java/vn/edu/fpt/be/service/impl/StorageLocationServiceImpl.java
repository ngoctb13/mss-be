package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.dto.StorageLocationRequest;
import vn.edu.fpt.be.model.StorageLocation;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StorageLocationRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.StorageLocationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {
    private final StorageLocationRepository repo;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public StorageLocationDTO createStorageLocation(StorageLocationRequest storageLocationRequest) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());
        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(storageLocationRequest.getStoreId()));
        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + storageLocationRequest.getStoreId() + " is not owned by the current user.");
        }
        // Retrieve the store based on the provided storeId
        Store store = storeRepository.findById(storageLocationRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

//        StorageLocation storageLocation = modelMapper.map(storageLocationRequest, StorageLocation.class);
        StorageLocation storageLocation = new StorageLocation();
        storageLocation.setLocationName(storageLocationRequest.getLocationName());
        storageLocation.setCapacity(storageLocationRequest.getCapacity());
        storageLocation.setDescription(storageLocationRequest.getDescription());
        storageLocation.setStatus(Status.ACTIVE);
        storageLocation.setStore(store);
        storageLocation.setCreatedBy(currentUser.get().getUsername());
        StorageLocation savedStorageLocation = repo.save(storageLocation);
        return modelMapper.map(savedStorageLocation, StorageLocationDTO.class);
    }

    @Override
    public StorageLocationDTO updateStorageLocation(StorageLocationRequest storageLocationRequest, Long storageLocationId) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        StorageLocation existingStorageLocation = repo.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Storage location not found"));
        // Retrieve the store based on the provided storeId
        Store store = storeRepository.findById(storageLocationRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        existingStorageLocation.setLocationName(storageLocationRequest.getLocationName());
        existingStorageLocation.setCapacity(storageLocationRequest.getCapacity());
        existingStorageLocation.setDescription(storageLocationRequest.getDescription());
        existingStorageLocation.setStatus(Status.ACTIVE);
        existingStorageLocation.setStore(store);

        StorageLocation updatedStorageLocation = repo.save(existingStorageLocation);
        return modelMapper.map(updatedStorageLocation, StorageLocationDTO.class);
    }

    @Override
    public List<StorageLocationDTO> getByStore(Long storeId) {
        List<StorageLocation> storageLocations = repo.findByStoreId(storeId);
        return storageLocations.stream()
                .map(storageLocation -> modelMapper.map(storageLocation, StorageLocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StorageLocationDTO deactivate(Long storageLocationId) {
        StorageLocation existingStorageLocation = repo.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Storage location not found"));
        existingStorageLocation.setStatus(Status.INACTIVE);
        StorageLocation deactivatedStorageLocation = repo.save(existingStorageLocation);
        return modelMapper.map(deactivatedStorageLocation, StorageLocationDTO.class);
    }
}
