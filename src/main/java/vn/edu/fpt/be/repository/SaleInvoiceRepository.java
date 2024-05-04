package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.SaleInvoice;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Long> {
    List<SaleInvoice> findByCustomerIdAndStoreIdOrderByCreatedAtDesc(Long customerId, Long storeId);
    @Query("SELECT si FROM SaleInvoice si WHERE (:startDate IS NULL OR si.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR si.createdAt <= :endDate) " +
            "AND (:createdBy IS NULL OR si.createdBy = :createdBy) " +
            "AND (:customerId IS NULL OR si.customer.id = :customerId) " +
            "AND si.store.id = :storeId " +
            "ORDER BY si.createdAt DESC") // Thêm dòng này để sắp xếp
    List<SaleInvoice> findInvoicesByCriteria(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("createdBy") String createdBy,
                                             @Param("customerId") Long customerId,
                                             @Param("storeId") Long storeId);

    @Query("SELECT si FROM SaleInvoice si WHERE si.createdAt >= :startDate AND si.store.id = :storeId ORDER BY si.createdAt DESC")
    List<SaleInvoice> findRecentInvoicesByStoreId(@Param("storeId") Long storeId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT si FROM SaleInvoice si WHERE si.store.id = :storeId ORDER BY si.createdAt ASC")
    List<SaleInvoice> findAllByStoreIdOrderByCreatedAtAsc(@Param("storeId") Long storeId);

}
