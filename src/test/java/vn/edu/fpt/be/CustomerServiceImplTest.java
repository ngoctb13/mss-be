package vn.edu.fpt.be;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.request.CustomerUpdateReq;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.service.UserService;
import vn.edu.fpt.be.service.impl.CustomerServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {


    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private User currentUser;
    private Store store;
    private Customer customer;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setId(1L);

        currentUser = new User();
        currentUser.setStore(store);

        customer = new Customer();
        customer.setId(1L);
        customer.setCustomerName("Test Customer");
        customer.setPhoneNumber("1234567890");
        customer.setAddress("Test Address");
        customer.setTotalDebt(100.0);
        customer.setStatus(Status.ACTIVE);
        customer.setStore(store);
    }

    @Test
    void testCreateCustomer() {
        CustomerCreateDTO customerCreateDTO = new CustomerCreateDTO();
        customerCreateDTO.setCustomerName("New Customer");
        customerCreateDTO.setPhoneNumber("9876543210");
        customerCreateDTO.setAddress("New Address");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDTO createdCustomerDTO = customerService.createCustomer(customerCreateDTO);

        assertNotNull(createdCustomerDTO);
        assertEquals(customer.getCustomerName(), createdCustomerDTO.getCustomerName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        CustomerUpdateReq updateReq = new CustomerUpdateReq();
        updateReq.setCustomerName("Updated Customer");
        updateReq.setPhoneNumber("0987654321");
        updateReq.setAddress("Updated Address");
        updateReq.setNote("Updated Note");

        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findByIdAndStoreId(1L, currentUser.getStore().getId())).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);

        CustomerDTO updatedCustomerDTO = customerService.updateCustomer(updateReq, 1L);

        assertNotNull(updatedCustomerDTO);
        assertEquals(updateReq.getCustomerName(), updatedCustomerDTO.getCustomerName());
        assertEquals(updateReq.getPhoneNumber(), updatedCustomerDTO.getPhoneNumber());
        assertEquals(updateReq.getAddress(), updatedCustomerDTO.getAddress());
        assertEquals(updateReq.getNote(), updatedCustomerDTO.getNote());
        verify(customerRepository, times(1)).findByIdAndStoreId(1L, currentUser.getStore().getId());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetCustomerById() {
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO customerDTO = customerService.getCustomerById(1L);

        assertNotNull(customerDTO);
        assertEquals(customer.getCustomerName(), customerDTO.getCustomerName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllCustomers() {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Customer> customerPage = new PageImpl<>(Arrays.asList(customer));

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        List<CustomerDTO> customerDTOs = customerService.getAllCustomers(pageNumber, pageSize);

        assertNotNull(customerDTOs);
        assertEquals(1, customerDTOs.size());
        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetCustomersByStore() {
        List<Customer> customerList = Arrays.asList(customer);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findByStoreId(currentUser.getStore().getId())).thenReturn(customerList);

        List<CustomerDTO> customerDTOs = customerService.getCustomersByStore();

        assertNotNull(customerDTOs);
        assertEquals(1, customerDTOs.size());
        verify(customerRepository, times(1)).findByStoreId(currentUser.getStore().getId());
    }

    @Test
    void testGetCustomersTotalDebtGreaterThan() {
        double totalDebt = 50.0;
        List<Customer> customerList = Arrays.asList(customer);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findByStoreIdAndTotalDebtGreaterThan(currentUser.getStore().getId(), totalDebt)).thenReturn(customerList);

        List<CustomerDTO> customerDTOs = customerService.getCustomersTotalDebtGreaterThan(totalDebt);

        assertNotNull(customerDTOs);
        assertEquals(1, customerDTOs.size());
        verify(customerRepository, times(1)).findByStoreIdAndTotalDebtGreaterThan(currentUser.getStore().getId(), totalDebt);
    }

    @Test
    void testDeactivateCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO deactivatedCustomerDTO = customerService.deactivate(1L);

        assertNotNull(deactivatedCustomerDTO);
        assertEquals(Status.INACTIVE, deactivatedCustomerDTO.getStatus());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testGetAllCustomerOrderByTotalDebt() {
        List<Customer> customerList = Arrays.asList(customer, new Customer());
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findByStoreIdOrderByTotalDebtDesc(currentUser.getStore().getId())).thenReturn(customerList);

        List<CustomerDTO> customerDTOs = customerService.getAllCustomerOrderByTotalDebt();

        assertNotNull(customerDTOs);
        assertEquals(2, customerDTOs.size());
        verify(customerRepository, times(1)).findByStoreIdOrderByTotalDebtDesc(currentUser.getStore().getId());
    }
    @Test
    void testGetAllCustomerHaveDebt_CustomerType() {
        String type = "CUSTOMER";
        List<Customer> customerList = Arrays.asList(customer);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findCustomersHaveDebt(currentUser.getStore().getId())).thenReturn(customerList);

        List<CustomerDTO> customerDTOs = customerService.getAllCustomerHaveDebt(type);

        assertNotNull(customerDTOs);
        assertEquals(1, customerDTOs.size());
        verify(customerRepository, times(1)).findCustomersHaveDebt(currentUser.getStore().getId());
    }

    @Test
    void testGetAllCustomerHaveDebt_OwnerType() {
        String type = "OWNER";
        List<Customer> customerList = Arrays.asList(customer);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(customerRepository.findCustomersWhichOwnerHaveDebt(currentUser.getStore().getId())).thenReturn(customerList);

        List<CustomerDTO> customerDTOs = customerService.getAllCustomerHaveDebt(type);

        assertNotNull(customerDTOs);
        assertEquals(1, customerDTOs.size());
        verify(customerRepository, times(1)).findCustomersWhichOwnerHaveDebt(currentUser.getStore().getId());
    }
}