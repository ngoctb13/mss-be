package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.PersonalDebt;
import vn.edu.fpt.be.model.enums.DebtType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDebtHistoryResponse {
    private Long id;
    private LocalDateTime createdAt;
    private Double amount;
    private DebtType type;
    private String note;
    private PersonalDebt personalDebt;
}
