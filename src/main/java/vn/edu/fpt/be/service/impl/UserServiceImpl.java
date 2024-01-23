package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.auth.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Override
    public User findUserByJwt(String jwt) {
        jwt = jwt.substring(7);
        String username = jwtService.extractUsername(jwt);

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public boolean isStoreOwnerOfStore(Long userId, Long storeId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Store> store = storeRepository.findById(storeId);

        if (user.isPresent() && store.isPresent()) {
            return store.get().getOwner().equals(user.get());
        }
        return false;
    }
    @Override
    public boolean isUserIsStoreOwner(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get().getRole().equals("STORE_OWNER");
        }
        return false;
    }

}
