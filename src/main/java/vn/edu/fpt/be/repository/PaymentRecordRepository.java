package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.PaymentRecord;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
}
