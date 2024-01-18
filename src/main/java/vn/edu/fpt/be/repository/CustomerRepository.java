package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
