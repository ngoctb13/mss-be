package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.dto.StoreUpdateDTO;
import vn.edu.fpt.be.model.Store;

import java.util.List;

public interface StoreService {
    StoreCreateDTO createStore(StoreCreateDTO storeCreateDTO);
    List<StoreDTO> getAllStores();
    List<StoreDTO> getStoresByOwner();
    StoreDTO updateStore(Long storeId,StoreUpdateDTO storeUpdateDTO);
    StoreDTO deactivateStore(Long storeId);
}
