package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebtRecordResponse {
    private Long id;
    private Customer customer;
    private Double debtAmount;
    private String note;
}
