package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.DebtPaymentHistory;

public interface DebtPaymentHistoryRepository extends JpaRepository<DebtPaymentHistory, Long> {
}
