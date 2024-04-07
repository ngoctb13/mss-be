package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDebtHistoryUpdateReq {
    private Double amount;
    private String note;
}
