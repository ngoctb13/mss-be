package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecordRequest {
    private Long customerId;
    private LocalDateTime recordDate;
    private Double paymentAmount;
    private String note;
}
