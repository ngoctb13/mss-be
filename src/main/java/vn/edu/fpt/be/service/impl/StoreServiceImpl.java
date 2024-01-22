package vn.edu.fpt.be.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.service.StoreService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Store createStore(StoreAddDTO storeAddDTO) {
        Store store = modelMapper.map(storeAddDTO, Store.class);
        return storeRepository.save(store);
    }

    @Override
    public Store getStore(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        return store.orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    @Override
    public Store updateStore(Long storeId, StoreAddDTO storeAddDTO) {
        Store store = getStore(storeId);
        modelMapper.map(storeAddDTO, store);
        return storeRepository.save(store);
    }

    @Override
    public void deactivateStore(Long storeId) {
        Store store = new Store();
        store.setStatus(Status.INACTIVE);
        storeRepository.save(store);
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // Example method to convert entity list to DTO list
    public List<StoreAddDTO> getAllStoreDTOs() {
        return getAllStores().stream()
                .map(store -> modelMapper.map(store, StoreAddDTO.class))
                .collect(Collectors.toList());
    }
}
