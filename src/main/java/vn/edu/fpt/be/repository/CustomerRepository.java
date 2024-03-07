package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStoreId(Long storeId);
    List<Customer> findByStoreIdAndTotalDebtGreaterThan(Long storeId, double totalDebt);
}
