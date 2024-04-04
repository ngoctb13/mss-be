package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.PersonalDebtHistory;
import vn.edu.fpt.be.model.enums.DebtType;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalDebtHistoryRepository extends JpaRepository<PersonalDebtHistory, Long> {
    List<PersonalDebtHistory> findByPersonalDebtIdOrderByCreatedAtDesc(Long personalDebtId);
    @Query("SELECT pdh FROM PersonalDebtHistory pdh " +
            "WHERE pdh.personalDebt.id = :personalDebtId " +
            "AND (:startDate IS NULL OR pdh.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR pdh.createdAt <= :endDate) " +
            "AND (:type IS NULL OR pdh.type = :type) " +
            "ORDER BY pdh.createdAt DESC")
    List<PersonalDebtHistory> findByDateRangeAndType(
            @Param("personalDebtId") Long personalDebtId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") DebtType type);
}
