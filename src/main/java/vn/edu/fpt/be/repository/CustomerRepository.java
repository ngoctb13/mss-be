package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Customer;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStoreId(Long storeId);
    Customer findByIdAndStoreId(Long customerId, Long storeId);
    List<Customer> findByStoreIdAndTotalDebtGreaterThan(Long storeId, double totalDebt);
    List<Customer> findByStoreIdOrderByTotalDebtDesc(Long storeId);
    @Query("SELECT c FROM Customer c WHERE c.store.id = :storeId AND c.totalDebt >= 0 ORDER BY c.totalDebt DESC")
    List<Customer> findCustomersHaveDebt(@Param("storeId") Long storeId);
    @Query("SELECT c FROM Customer c WHERE c.store.id = :storeId AND c.totalDebt < 0 ORDER BY c.totalDebt ASC")
    List<Customer> findCustomersWhichOwnerHaveDebt(@Param("storeId") Long storeId);

}
