package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.model.Store;

import java.util.List;

public interface StoreService {
    Store createStore(StoreAddDTO storeAddDTO);
    Store getStore(Long storeId);
    Store updateStore(Long storeId, StoreAddDTO storeAddDTO);
    void deactivateStore(Long storeId);
    List<Store> getAllStores();
}