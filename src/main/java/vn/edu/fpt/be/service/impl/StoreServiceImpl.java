package vn.edu.fpt.be.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.StoreAddDTO;
import vn.edu.fpt.be.dto.StoreUpdateDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.StoreService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public StoreAddDTO createStore(StoreAddDTO storeAddDTO) {
        Store store = modelMapper.map(storeAddDTO, Store.class);
        User owner = store.getOwner();
        if (owner != null && owner.getUserId() == null) {
            userRepository.save(owner);
        }

        Store savedStore = storeRepository.save(store);
        return modelMapper.map(savedStore,StoreAddDTO.class);
    }

    @Override
    public Store getStore(Long storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        return store.orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    @Override
    public StoreUpdateDTO updateStore(Long storeId, StoreUpdateDTO storeUpdateDTO) {
        Store store = modelMapper.map(storeUpdateDTO, Store.class);
        Store saveStore = storeRepository.save(store);
        return modelMapper.map(saveStore,StoreUpdateDTO.class);
    }

    @Override
    public Store deactivateStore(Long storeId) {
        Store store = new Store();
        store.setStatus(Status.INACTIVE);
        return storeRepository.save(store);
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
