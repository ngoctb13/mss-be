package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.DebtRecord;

public interface DebtRecordRepository extends JpaRepository<DebtRecord, Long> {
}
