package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.CustomerRequestDTO;
import java.util.List;

public interface CustomerService {
    void createCustomer(CustomerRequestDTO customerRequestDTO);
    List<CustomerDTO> getAllCustomer();
    CustomerDTO getCustomerId();
}
