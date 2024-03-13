package vn.edu.fpt.be.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.dto.response.SaleInvoiceReportResponse;
import vn.edu.fpt.be.dto.response.StoreResponse;
import vn.edu.fpt.be.exception.CustomServiceException;
import vn.edu.fpt.be.model.SaleInvoice;
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
            userRepository.save(currentUser);

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
//        try {
//            List<Store> stores = storeRepository.findAll();
//            return stores.stream()
//                    .map(this::convertToDto)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch all stores.", e);
//        }
        return null;
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

//    @Override
//    @Transactional(readOnly = true)
    public List<StoreResponse> getStoreByFilter(String storeName, String address, String phoneNumber, String status) {
        try {
            User currentUser = userService.getCurrentUser();
            Store currentStore = currentUser.getStore();
            List<Store> storeDTOS = storeRepository.findByCriteria(storeName, address, phoneNumber, status);
            return storeDTOS.stream().map(stores -> StoreResponse.builder()
                            .id(stores.getId())
                            .createdAt(stores.getCreatedAt())
                            .createdBy(stores.getCreatedBy())
                            .storeName(stores.getStoreName())
                            .address(stores.getAddress())
                            .phoneNumber(stores.getPhoneNumber())
                            .status(stores.getStatus())
                            .owner(currentUser)
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CustomServiceException("Error accessing the database", e);
        }
    }

    private StoreDTO convertToStoreDto(Long ownerId,Store store) {
        Optional<User> currentUser = userRepository.findById(ownerId);
        // Assuming you have a method to convert Store to StoreDTO. Adjust the fields as per your StoreDTO class.
        return new StoreDTO(
                store.getId(),
                store.getStoreName(),
                store.getAddress(),
                currentUser.get(),
                store.getStatus().toString() // Assuming status is an enum and needs to be converted to string
        );
    }



    private StoreDTO convertToDto(Store store) {
//        return StoreDTO.builder()
//                .id(store.getId())
//                .storeName(store.getStoreName())
//                .address(store.getAddress())
//                .owner(userRepository.findByStoreId(store.getId()))
//                .status(store.getStatus().toString()) // Convert Status enum to String
//                .build();
        return null;
    }

}
