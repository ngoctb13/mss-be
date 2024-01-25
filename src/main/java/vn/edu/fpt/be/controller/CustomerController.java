package vn.edu.fpt.be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.service.CustomerService;
import vn.edu.fpt.be.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customerDTO, @RequestHeader("Authorization") String jwt) {

        User authUser = userService.findUserByJwt(jwt);
        Long storeId = customerDTO.getStoreId();

        if (userService.isStoreOwnerOfStore(authUser.getUserId(), storeId)) {
            throw new AccessDeniedException("The specified store does not belong to the authenticated store owner.");
        }

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomer(customerDTO, customerId);
        return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomer();
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<List<CustomerDTO>> getCustomersByStoreId(@PathVariable Long storeId) {
        List<CustomerDTO> customers = customerService.getCustomersByStoreId(storeId);
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }
    @GetMapping("/search/{searchTerm}")
    @PreAuthorize("hasRole('STORE_OWNER')")
    public ResponseEntity<List<CustomerDTO>> getCustomersByNameOrPhoneNumber(@PathVariable(required = false) String searchTerm) {
            List<CustomerDTO> customers = customerService.getCustomersByNameOrPhoneNumber(searchTerm);
            return new ResponseEntity<>(customers, HttpStatus.OK);
    }
}
