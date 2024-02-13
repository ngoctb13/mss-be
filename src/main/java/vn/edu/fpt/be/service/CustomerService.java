package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.CustomerUpdateDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO, Long storeId);
    List<CustomerDTO> getAllCustomers();
    List<CustomerDTO> getCustomersByStore(Long storeId);
    CustomerDTO updateCustomer(Long customerId,CustomerUpdateDTO customerUpdateDTO);
    CustomerDTO deactivateCustomer(Long customerId);
}
