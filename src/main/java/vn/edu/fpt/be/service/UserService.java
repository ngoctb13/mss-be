package vn.edu.fpt.be.service;

import vn.edu.fpt.be.model.User;

public interface UserService {
    public User findUserByJwt(String jwt);
    boolean isStoreOwnerOfStore(Long userId, Long storeId);
}
