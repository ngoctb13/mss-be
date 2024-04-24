package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import vn.edu.fpt.be.dto.StoreCreateDTO;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.StoreServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private StoreServiceImpl storeService;

    private User currentUser;
    private Store store;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(Role.STORE_OWNER);

        store = new Store();
        store.setId(1L);
        store.setStoreName("Test Store");
        store.setAddress("Test Address");
        store.setPhoneNumber("1234567890");
        store.setStatus(Status.ACTIVE);
        store.setCreatedBy("testuser");

        currentUser.setStore(store);
    }

    @Test
    void testCreateStore() {
        StoreCreateDTO storeCreateDTO = new StoreCreateDTO();
        storeCreateDTO.setStoreName("New Store");
        storeCreateDTO.setAddress("New Address");
        storeCreateDTO.setPhoneNumber("9876543210");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        StoreDTO createdStoreDTO = storeService.createStore(storeCreateDTO);

        assertNotNull(createdStoreDTO);
        assertEquals(storeCreateDTO.getStoreName(), createdStoreDTO.getStoreName());
        assertEquals(storeCreateDTO.getAddress(), createdStoreDTO.getAddress());

        verify(storeRepository, times(1)).save(any(Store.class));
        verify(userRepository, times(1)).save(currentUser);
    }

    @Test
    void testGetStoreByOwner() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));

        StoreDTO storeDTO = storeService.getStoreByOwner(1L);

        assertNotNull(storeDTO);
        assertEquals(store.getStoreName(), storeDTO.getStoreName());
        assertEquals(store.getAddress(), storeDTO.getAddress());
        assertEquals(store.getStatus().toString(), storeDTO.getStatus());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testListAllStores() {
        Store anotherStore = new Store();
        anotherStore.setId(2L);
        anotherStore.setStoreName("Another Store");
        anotherStore.setAddress("Another Address");
        anotherStore.setStatus(Status.ACTIVE);

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setRole(Role.STORE_OWNER);
        anotherUser.setStore(anotherStore);

        List<Store> storeList = Arrays.asList(store, anotherStore);
        when(storeRepository.findAll()).thenReturn(storeList);
        when(userRepository.findStoreOwnerByStoreId(1L, Role.STORE_OWNER)).thenReturn(Optional.of(currentUser));
        when(userRepository.findStoreOwnerByStoreId(2L, Role.STORE_OWNER)).thenReturn(Optional.of(anotherUser));

        List<StoreDTO> storeDTOs = storeService.listAllStores();

        assertNotNull(storeDTOs);
        assertEquals(2, storeDTOs.size());
        verify(storeRepository, times(1)).findAll();
        verify(userRepository, times(1)).findStoreOwnerByStoreId(1L, Role.STORE_OWNER);
        verify(userRepository, times(1)).findStoreOwnerByStoreId(2L, Role.STORE_OWNER);
    }

    @Test
    void testDeactivateStore() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        StoreDTO deactivatedStoreDTO = storeService.deactivateStore(1L);

        assertNotNull(deactivatedStoreDTO);
        assertEquals(Status.INACTIVE, deactivatedStoreDTO.getStatus());
        verify(storeRepository, times(1)).findById(1L);
        verify(storeRepository, times(1)).save(store);
    }
}