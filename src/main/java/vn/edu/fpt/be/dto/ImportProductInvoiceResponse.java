package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.Supplier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportProductInvoiceResponse {
    private Long id;
    private Double oldDebt;
    private Double totalPayment;
    private Double pricePaid;
    private Double newDebt;
    private Supplier supplier;
    private Store store;
}
