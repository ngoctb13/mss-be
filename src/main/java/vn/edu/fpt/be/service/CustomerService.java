package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO deactivate(Long customerId);
}
