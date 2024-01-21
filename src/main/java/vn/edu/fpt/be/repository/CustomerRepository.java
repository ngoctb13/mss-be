package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStore_StoreId(Long storeId);
    List<Customer> findByCustomerNameContaining(String customerName);
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:searchTerm% OR c.phoneNumber LIKE %:searchTerm%")
    List<Customer> findByCustomerNameOrPhoneNumberContaining(@Param("searchTerm") String searchTerm);
}
