package vn.edu.fpt.be.dto.response;

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
public class PaymentRecordResponse {
    private Long id;
    private Customer customer;
    private Double paymentAmount;
    private LocalDateTime recordDate;
    private String note;
}
