package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.PersonalDebt;

import java.util.List;

public interface PersonalDebtRepository extends JpaRepository<PersonalDebt, Long> {
    List<PersonalDebt> findByStoreIdOrderByCreatedAtDesc(Long storeId);
}
