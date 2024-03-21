package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.DebtPaymentHistory;

import java.util.List;

public interface DebtPaymentHistoryRepository extends JpaRepository<DebtPaymentHistory, Long> {
    List<DebtPaymentHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
