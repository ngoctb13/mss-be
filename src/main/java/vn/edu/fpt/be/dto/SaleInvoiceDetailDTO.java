package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.SaleInvoice;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleInvoiceDetailDTO {
    private Long id;
    private Product product;
    private SaleInvoice saleInvoice;
    private Double quantity;
    private Double unitPrice;
    private Double totalPrice;
}
