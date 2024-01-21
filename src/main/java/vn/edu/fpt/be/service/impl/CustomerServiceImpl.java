package vn.edu.fpt.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.repository.CustomerRepository;
import vn.edu.fpt.be.service.CustomerService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        Customer savedCustomer = customerRepository.save(customer);

        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO, Long customerId) {
        Customer existCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        modelMapper.map(customerDTO, existCustomer);
        Customer updatedCustomer = customerRepository.save(existCustomer);
        return modelMapper.map(updatedCustomer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> getAllCustomer() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public List<CustomerDTO> getCustomersByStoreId(Long storeId) {
        List<Customer> customers = customerRepository.findByStore_StoreId(storeId);

        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .toList();
    }

    @Override
    public List<CustomerDTO> getCustomerByCustomerName(String customerName) {
        List<Customer> customers = customerRepository.findByCustomerNameContaining(customerName);
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getCustomersByNameOrPhoneNumber(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                List<Customer> allCustomers = customerRepository.findAll();
                return allCustomers.stream()
                        .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                        .collect(Collectors.toList());
            }
            List<Customer> customers = customerRepository.findByCustomerNameOrPhoneNumberContaining(searchTerm);
            return customers.stream()
                    .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new RuntimeException("An error occurred while retrieving customers. Please try again later.");
        }
    }

}
