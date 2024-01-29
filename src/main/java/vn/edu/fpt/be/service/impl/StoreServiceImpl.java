package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.StoreService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public StoreCreateDTO createStore(StoreCreateDTO storeCreateDTO) {
        try {
            UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());

            if (currentUser.isEmpty()) {
                throw new RuntimeException("Authenticated user not found.");
            }

            Store store = new Store();
            store.setStoreName(storeCreateDTO.getStoreName());
            store.setAddress(storeCreateDTO.getAddress());
            store.setOwner(currentUser.get());
            store.setStatus(Status.ACTIVE);
            store.setCreatedAt(new Date());
            store.setCreatedBy(currentUser.get().getUsername());

            return modelMapper.map(storeRepository.save(store), StoreCreateDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to create store: " + e.getMessage());
        }
    }
}
