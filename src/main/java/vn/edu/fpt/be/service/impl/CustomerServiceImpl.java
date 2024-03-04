package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Customer customer = new Customer();
        customer.setCustomerName(customerCreateDTO.getCustomerName());
        customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());
        customer.setAddress(customerCreateDTO.getAddress());
        customer.setTotalDebt(0.0);
        customer.setStatus(Status.ACTIVE);
        Optional<Store> ownedStores = storeRepository.findStoreById(currentUser.get().getId());
        Store store = ownedStores.stream()
                .filter(s -> s.getId().equals(currentUser.get().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Store not found with id: " + currentUser.get().getId())); // This should theoretically never happen due to the previous check
        customer.setStore(store);
        Customer saveCustomer= customerRepository.save(customer);
        return modelMapper.map(saveCustomer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> getAllCustomers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return customerPage.getContent().stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO deactivate(Long customerId) {
        UserPrincipal currentUserPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> currentUser = userRepository.findById(currentUserPrincipal.getId());
        if (currentUser.isEmpty()) {
            throw new RuntimeException("Authenticated user not found.");
        }
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (customer.getStatus().equals(Status.ACTIVE)){
            customer.setStatus(Status.INACTIVE);
        }else {
            customer.setStatus(Status.ACTIVE);
        }
        Customer saveCustomer= customerRepository.save(customer);
        return modelMapper.map(saveCustomer, CustomerDTO.class);
    }
}
