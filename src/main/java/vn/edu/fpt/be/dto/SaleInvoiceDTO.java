package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Customer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleInvoiceDTO {
    private Long id;
    private Double totalPrice;
    private Double oldDebt;
    private Double totalPayment;
    private Double pricePaid;
    private Double newDebt;
    private Customer customer;
}
