package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleInvoiceDetailRequest {
    private Long productId;
    private Double quantity;
    private Double unitPrice;
}
