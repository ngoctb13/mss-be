package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.RegisterRequestDTO;
import vn.edu.fpt.be.dto.StaffCreateDTO;
import vn.edu.fpt.be.model.Staff;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StaffRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.StaffService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    @Override
    public void createStaff(StaffCreateDTO staffCreateDTO) {
        Optional<User> existingUser = userRepository.findByUsername(staffCreateDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException(staffCreateDTO.getUsername() + " đã tồn tại!");
        }

        User staffUser = new User();
        staffUser.setUsername(staffCreateDTO.getUsername());
        staffUser.setPassword(passwordEncoder.encode(staffCreateDTO.getPassword()));
        staffUser.setRole(Role.STAFF);
        staffUser.setStatus(Status.ACTIVE);
        staffUser.setCreatedAt(new Date());
        userRepository.save(staffUser);

        Staff staff = new Staff();
        staff.setUser(staffUser);

        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        List<Store> ownedStores = storeRepository.findByOwnerId(currentUser.get().getId());

        // Check if the provided storeId is in the list of ownedStores
        boolean isStoreOwnedByCurrentUser = ownedStores.stream()
                .anyMatch(store -> store.getId().equals(staffCreateDTO.getStoreId()));

        if (!isStoreOwnedByCurrentUser) {
            throw new IllegalArgumentException("The store with ID " + staffCreateDTO.getStoreId() + " is not owned by the current user.");
        }
        // Assuming the check passes, find the store to set in the Staff object
        Store store = ownedStores.stream()
                .filter(s -> s.getId().equals(staffCreateDTO.getStoreId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Store not found with id: " + staffCreateDTO.getStoreId())); // This should theoretically never happen due to the previous check
        staff.setStore(store);

        staffRepository.save(staff);
    }
}
