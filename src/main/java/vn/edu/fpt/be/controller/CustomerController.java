package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.StaffCreateDTO;
import vn.edu.fpt.be.service.CustomerService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerCreateDTO customerCreateDTO,
                                            @PathVariable Long storeId) {
        try {
            CustomerDTO createdCustomer = customerService.createCustomer(customerCreateDTO, storeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the customer.");
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<?> getAllCustomers(@RequestParam(defaultValue = "0") int pageNumber,
                                             @RequestParam(defaultValue = "5") int pageSize) {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers(pageNumber, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/by-store/{storeId}")
    @PreAuthorize("hasAuthority('STORE_OWNER')")
    public ResponseEntity<?> getCustomersByStore(@PathVariable Long storeId) {
        try {
            List<CustomerDTO> customers = customerService.getCustomersByStore(storeId);
            if (customers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
