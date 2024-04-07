package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.enums.RecordType;
import vn.edu.fpt.be.model.enums.SourceType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebtPaymentResponse {
    private Long id;
    private LocalDateTime createdAt;
    private Customer customer;
    private RecordType type;
    private Long sourceId;
    private SourceType sourceType;
    private Double amount;
    private LocalDateTime recordDate;
    private String note;
}
