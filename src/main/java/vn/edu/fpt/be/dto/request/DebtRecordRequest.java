package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DebtRecordRequest {
    private Long customerId;
    private LocalDateTime recordDate;
    private Double debtAmount;
    private String note;
}
