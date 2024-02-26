package vn.edu.fpt.be.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.StoreService;
import vn.edu.fpt.be.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public StoreDTO createStore(StoreCreateDTO storeCreateDTO) {
        try {
            User currentUser = userService.getCurrentUser();

            Store newStore = new Store();
            newStore.setStoreName(storeCreateDTO.getStoreName());
            newStore.setAddress(storeCreateDTO.getAddress());
            newStore.setPhoneNumber(storeCreateDTO.getPhoneNumber());
            newStore.setStatus(Status.ACTIVE);
            newStore.setCreatedBy(currentUser.getUsername());

            Store savedStore = storeRepository.save(newStore);
            currentUser.setStore(savedStore);

            StoreDTO savedStoreDTO = new StoreDTO();
            savedStoreDTO.setStoreName(savedStore.getStoreName());
            savedStoreDTO.setId(savedStore.getId());
            savedStoreDTO.setAddress(savedStore.getAddress());
            savedStoreDTO.setStatus(savedStoreDTO.getStatus());
            savedStoreDTO.setOwner(currentUser);
            return savedStoreDTO;
        } catch (Exception e) {
            throw new RuntimeException("Fail to create store: " + e.getMessage());
        }
    }

    @Override
    public List<StoreDTO> getAllStores() {
        try {
            List<Store> stores = storeRepository.findAll();
            return stores.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all stores.", e);
        }
    }

    @Override
    public StoreDTO getStoreByOwner(Long ownerId) {
        try {
            Optional<User> currentUser = userRepository.findById(ownerId);
            if (currentUser.isEmpty()) {
                throw new RuntimeException("User not found with id " + ownerId);
            }
            Store store = currentUser.get().getStore();

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setStoreName(store.getStoreName());
            storeDTO.setId(store.getId());
            storeDTO.setAddress(store.getAddress());
            storeDTO.setStatus(String.valueOf(store.getStatus()));
            storeDTO.setOwner(currentUser.get());
            return storeDTO;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch store by owner.", e);
        }
    }

    private StoreDTO convertToDto(Store store) {
        return StoreDTO.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .owner(userRepository.findByStoreId(store.getId()))
                .status(store.getStatus().toString()) // Convert Status enum to String
                .build();
    }
}
