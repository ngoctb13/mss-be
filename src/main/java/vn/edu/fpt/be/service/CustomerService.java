package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO, Long customerId);
    List<CustomerDTO> getAllCustomer();
    List<CustomerDTO> getCustomersByStoreId(Long storeId);
    List<CustomerDTO> getCustomerByCustomerName(String customerName);
    List<CustomerDTO> getCustomersByNameOrPhoneNumber(String searchTerm);
}
