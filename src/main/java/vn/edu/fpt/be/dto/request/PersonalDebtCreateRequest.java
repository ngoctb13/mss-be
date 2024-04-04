package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDebtCreateRequest {
    private String creditorName;
    private String creditorPhone;
    private String creditorAddress;
    private Double amount;
    private String note;
}
