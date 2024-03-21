package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRecordRequest {
    private Long customerId;
    private Double paymentAmount;
    private String note;
}
