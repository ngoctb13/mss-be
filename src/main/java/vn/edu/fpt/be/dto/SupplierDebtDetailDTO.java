package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDebtDetailDTO {
    private Long id;
    private Product product;
    private Supplier supplier;
    private SupplierDebtInvoice supplierDebtInvoice;
    private Double quantity;
    private Double unitPrice;
    private Double unitPricePerDistance;
    private Double totalPrice;
    private Zone zone;
}
