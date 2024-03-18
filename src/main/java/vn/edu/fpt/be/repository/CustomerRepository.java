package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.enums.Status;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByStoreId(Long storeId);
    Customer findByIdAndStoreId(Long customerId, Long storeId);
    List<Customer> findByStoreIdAndTotalDebtGreaterThan(Long storeId, double totalDebt);
    @Query("Select c1 from Customer c1 where " +
            "(:customerName is null or c1.customerName= :customerName) " +
            "and (:phoneNumber is null or c1.phoneNumber= :phonenNumber) " +
            "and (:address is null or c1.address= :address) " +
            "and (c1.totalDebt between nullif(:minDebt,0) and nullif(:maxdebt,999999999999))" )
    List<Customer> findCustomerByCriteria(@Param("customerName") String customerName,
                                          @Param("phoneNumber") String phoneNumber,
                                          @Param("address") String address,
                                          @Param("minDebt") Double minDebt,
                                          @Param("maxDebt") Double maxDebt,
                                          @Param("status") Status status);
}
