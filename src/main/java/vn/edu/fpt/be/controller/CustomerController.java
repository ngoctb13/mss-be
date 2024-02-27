package vn.edu.fpt.be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.be.dto.CustomerCreateDTO;
import vn.edu.fpt.be.dto.CustomerDTO;
import vn.edu.fpt.be.dto.ProductCreateDTO;
import vn.edu.fpt.be.dto.ProductDTO;
import vn.edu.fpt.be.service.CustomerService;
import vn.edu.fpt.be.service.ProductService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('STORE_OWNER')")

    public ResponseEntity<?> createCustomer(@RequestBody CustomerCreateDTO customerCreateDTO) {
        try {
            CustomerDTO createdProduct = customerService.createCustomer(customerCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the product.");
        }
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','STORE_OWNER')")
    public ResponseEntity<?> getAllCustomers(@RequestParam(defaultValue = "0") int pageNumber,
                                             @RequestParam(defaultValue = "5") int pageSize) {
        try {
            List<CustomerDTO> customers = customerService.getAllCustomers(pageNumber, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(customers);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/change-status")
    @PreAuthorize("hasAuthority('STORE_OWNER')")

    public ResponseEntity<?> createCustomer(@PathVariable Long customerId) {
        try {
            CustomerDTO changeStatusCustomer = customerService.deactivate(customerId);
            return ResponseEntity.status(HttpStatus.OK).body(changeStatusCustomer);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the product.");
        }
    }
}
