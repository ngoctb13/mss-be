package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.dto.StoreUpdateDTO;
import vn.edu.fpt.be.model.Store;

import java.util.List;

public interface StoreService {
    StoreAddDTO createStore(StoreAddDTO storeAddDTO);
    Store getStore(Long storeId);
    StoreUpdateDTO updateStore(Long storeId, StoreUpdateDTO storeUpdateDTO);
    Store deactivateStore(Long storeId);
    List<Store> getAllStores();

}