package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;
import vn.edu.fpt.be.model.Supplier;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportInvoiceReportResponse {
    private Long id;
    private LocalDateTime createdAt;
    private String createdBy;
    private Double totalPrice;
    private Double oldDebt;
    private Double totalPayment;
    private Double pricePaid;
    private Double newDebt;
    private Customer customer;
}
