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
import vn.edu.fpt.be.service.UserService;

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
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public StorageLocationDTO createStorageLocation(StorageLocationRequest storageLocationRequest) {
        User currentUser = userService.getCurrentUser();
        Store ownedStore = currentUser.getStore();

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
        Store store = currentUser.getStore();
        List<StorageLocation> storageLocations = repo.findByStoreId(store.getId());
        return storageLocations.stream()
                .map(storageLocation -> modelMapper.map(storageLocation, StorageLocationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public StorageLocationDTO deactivate(Long storageLocationId) {
        StorageLocation existingStorageLocation = repo.findById(storageLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Storage location not found"));
        if (existingStorageLocation.getStatus()== Status.INACTIVE){
            existingStorageLocation.setStatus(Status.ACTIVE);
        }else {
            existingStorageLocation.setStatus(Status.INACTIVE);
        }
        StorageLocation deactivatedStorageLocation = repo.save(existingStorageLocation);
        return modelMapper.map(deactivatedStorageLocation, StorageLocationDTO.class);
    }
}
