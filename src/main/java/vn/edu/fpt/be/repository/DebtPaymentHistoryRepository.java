package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.DebtPaymentHistory;

import java.time.LocalDateTime;
import java.util.List;

public interface DebtPaymentHistoryRepository extends JpaRepository<DebtPaymentHistory, Long> {
    List<DebtPaymentHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @Query("SELECT d FROM DebtPaymentHistory d WHERE d.customer.id = :customerId " +
            "AND (:startDate IS NULL OR d.recordDate >= :startDate) " +
            "AND (:endDate IS NULL OR d.recordDate <= :endDate) " +
            "ORDER BY d.recordDate DESC")
    List<DebtPaymentHistory> findByCustomerIdAndOptionalCreatedAtRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
