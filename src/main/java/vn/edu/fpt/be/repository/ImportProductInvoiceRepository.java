package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.ImportProductInvoice;
import vn.edu.fpt.be.model.SaleInvoice;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportProductInvoiceRepository extends JpaRepository<ImportProductInvoice, Long> {
    List<ImportProductInvoice> findByCustomerIdAndStoreIdOrderByCreatedAtDesc(Long customerId, Long storeId);
    @Query("SELECT si FROM ImportProductInvoice si WHERE (:startDate IS NULL OR si.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR si.createdAt <= :endDate) " +
            "AND (:createdBy IS NULL OR si.createdBy = :createdBy) " +
            "AND (:customerId IS NULL OR si.customer.id = :customerId) " +
            "AND si.store.id = :storeId")
    List<ImportProductInvoice> findInvoicesByCriteria(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("createdBy") String createdBy,
                                             @Param("customerId") Long customerId,
                                             @Param("storeId") Long storeId);
}
