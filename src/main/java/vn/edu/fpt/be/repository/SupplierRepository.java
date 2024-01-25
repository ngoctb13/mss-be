package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Supplier;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByStore_StoreId(Long storeId);
    @Query("SELECT s FROM Supplier s WHERE s.supplierName LIKE %:searchTerm% OR s.phoneNumber LIKE %:searchTerm%")
    List<Supplier> findBySupplierNameOrPhoneNumberContaining(@Param("searchTerm") String searchTerm);
}
