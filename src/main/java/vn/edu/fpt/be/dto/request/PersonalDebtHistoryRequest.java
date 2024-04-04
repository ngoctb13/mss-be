package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.DebtType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDebtHistoryRequest {
    private Double amount;
    private DebtType type;
    private String note;
}
