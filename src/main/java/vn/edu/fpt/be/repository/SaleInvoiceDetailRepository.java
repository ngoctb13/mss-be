package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.SaleInvoiceDetail;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleInvoiceDetailRepository extends JpaRepository<SaleInvoiceDetail, Long> {
    @Query("SELECT sid FROM SaleInvoiceDetail sid WHERE (:customerId IS NULL OR sid.saleInvoice.customer.id = :customerId) AND (:startDate IS NULL OR sid.createdAt >= :startDate) AND (:endDate IS NULL OR sid.createdAt <= :endDate) AND sid.saleInvoice.store.id = :storeId")
    List<SaleInvoiceDetail> findByCriteria(@Param("customerId") Long customerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("storeId") Long storeId);
    List<SaleInvoiceDetail> findBySaleInvoiceId(Long saleInvoiceId);
    @Query("SELECT sid FROM SaleInvoiceDetail sid WHERE sid.saleInvoice.store.id = :storeId AND FUNCTION('MONTH', sid.createdAt) = :month AND FUNCTION('YEAR', sid.createdAt) = :year")
    List<SaleInvoiceDetail> findByStoreIdAndMonthAndYear(@Param("storeId") Long storeId, @Param("month") int month, @Param("year") int year);

}
