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
import vn.edu.fpt.be.dto.response.CustomerSaleInvoiceResponse;
import vn.edu.fpt.be.model.*;
import vn.edu.fpt.be.model.enums.Status;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.repository.StoreRepository;
import vn.edu.fpt.be.repository.UserRepository;
import vn.edu.fpt.be.security.UserPrincipal;
import vn.edu.fpt.be.service.CustomerService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    @Override
    public CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO) {
        User currentUser = userService.getCurrentUser();
        Customer customer = new Customer();
        customer.setCustomerName(customerCreateDTO.getCustomerName());
        customer.setPhoneNumber(customerCreateDTO.getPhoneNumber());
        customer.setAddress(customerCreateDTO.getAddress());
        customer.setTotalDebt(0.0);
        customer.setStatus(Status.ACTIVE);
        customer.setStore(currentUser.getStore());
        customer.setCreatedBy(currentUser.getUsername());
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
    public List<CustomerDTO> getCustomersTotalDebtGreaterThan(double totalDebt) {
        try {
            User currentUser = userService.getCurrentUser();
            List<Customer> customers = customerRepository.findByStoreIdAndTotalDebtGreaterThan(currentUser.getStore().getId(), totalDebt);
            // Convert SaleInvoice entities to CustomerSaleInvoiceResponse DTOs
            return customers.stream()
                    .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Handle the exception based on your application's requirement
            // For example, log the error and throw a custom exception or return an error response
            // Log the error (using a logging framework like SLF4J)
            // Logger.error("Error retrieving sale invoices for customer: {}", customerId, e);
            throw new RuntimeException("Error retrieving sale invoices for customer");
        }
    }

    @Override
    public CustomerDTO deactivate(Long customerId) {
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
