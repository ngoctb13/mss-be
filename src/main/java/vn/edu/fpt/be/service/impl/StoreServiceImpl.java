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
import vn.edu.fpt.be.dto.StoreUpdateDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.StoreService;

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
    private final ModelMapper modelMapper = new ModelMapper();
    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);
    UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
    @Override
    public StoreCreateDTO createStore(StoreCreateDTO storeCreateDTO) {
        try {


            if (currentUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found.");
            }

            Store store = new Store();
            store.setStoreName(storeCreateDTO.getStoreName());
            store.setAddress(storeCreateDTO.getAddress());
            store.setOwner(currentUser.get());
            store.setStatus(Status.ACTIVE);
            store.setCreatedAt(LocalDateTime.now());
            store.setCreatedBy(currentUser.get().getUsername());

            return modelMapper.map(storeRepository.save(store), StoreCreateDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to create store: " + e.getMessage());
        }
    }

    @Override
    public List<StoreDTO> getAllStores() {
        try {
            List<Store> stores = storeRepository.findAll();
            return stores.stream()
                    .map(store -> modelMapper.map(store, StoreDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all stores.", e);
        }
    }

    @Override
    public List<StoreDTO> getStoresByOwner() {
        try {
            UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Collection<? extends GrantedAuthority> authorities = currentUserPrincipal.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                // Log each authority
                logger.info("User has authority: {}", authority.getAuthority());
            }


            Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());

            if (currentUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found.");
            }

            List<Store> stores = storeRepository.findByOwnerId(currentUser.get().getId());
            return stores.stream()
                    .map(store -> modelMapper.map(store, StoreDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch stores by owner.", e);
        }
    }

    @Override
    public StoreDTO updateStore(Long storeId,StoreUpdateDTO storeUpdateDTO) {
        try {
            if (currentUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found.");
            }
            Store store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Customer not found"));
            store.setStoreName(storeUpdateDTO.getStoreName());
            store.setAddress(storeUpdateDTO.getAddress());
            store.setUpdatedAt(LocalDateTime.now());
            return modelMapper.map(storeRepository.save(store), StoreDTO.class);
        } catch (Exception e){
            throw new RuntimeException("Fail to update store: " + e.getMessage());
        }

    }

    @Override
    public StoreDTO deactivateStore(Long storeId) {
        try {
            if (currentUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found.");
            }
            Store store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Customer not found"));
            store.setStatus(Status.INACTIVE);
            store.setUpdatedAt(LocalDateTime.now());
            return modelMapper.map(storeRepository.save(store), StoreDTO.class);
        } catch (Exception e){
            throw new RuntimeException("Fail to update store: " + e.getMessage());
        }
    }
}
