package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.enums.RecordType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebtPaymentResponse {
    private Long id;
    private Customer customer;
    private RecordType type;
    private Long sourceId;
    private Double amount;
    private LocalDateTime recordDate;
    private String note;
}