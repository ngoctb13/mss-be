package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.model.Store;

import java.util.List;

public interface StoreService {
    StoreDTO createStore(StoreCreateDTO storeCreateDTO);
    List<StoreDTO> getAllStores();
    StoreDTO getStoreByOwner(Long ownerId);
    List<StoreDTO> listAllStores();
    StoreDTO deactivateStore(Long storeId);
}
