package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Supplier;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByStoreId(Long storeId);
    Supplier findByIdAndStoreId(Long supplierId, Long storeId);
    List<Supplier> findByStoreIdAndTotalDebtGreaterThan(Long storeId, double totalDebt);

}
