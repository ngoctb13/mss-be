package vn.edu.fpt.be.service;

import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.request.CustomerUpdateReq;

import java.util.List;

public interface CustomerService {
    CustomerDTO createCustomer(CustomerCreateDTO customerCreateDTO);
    CustomerDTO updateCustomer(CustomerUpdateReq req, Long customerId);
    CustomerDTO getCustomerById(Long customerId);
    List<CustomerDTO> getAllCustomers(int pageNumber, int pageSize);
    List<CustomerDTO> getCustomersByStore();
    List<CustomerDTO> getCustomersTotalDebtGreaterThan(double totalDebt);
    CustomerDTO deactivate(Long customerId);
    List<CustomerDTO> getAllCustomerOrderByTotalDebt();
    List<CustomerDTO> getAllCustomerHaveDebt(String type);
}
