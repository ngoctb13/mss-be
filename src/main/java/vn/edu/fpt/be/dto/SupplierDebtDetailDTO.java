package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.SaleInvoice;
import vn.edu.fpt.be.model.Supplier;
import vn.edu.fpt.be.model.Zone;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDebtDetailDTO {
    private Long id;
    private Product product;
    private Supplier supplier;
    private Double quantity;
    private Double unitPrice;
    private Double totalPrice;
    private Zone zone;
}
