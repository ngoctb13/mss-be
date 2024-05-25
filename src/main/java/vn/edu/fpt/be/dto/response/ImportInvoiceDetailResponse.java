package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.ImportProductInvoice;
import vn.edu.fpt.be.model.Product;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportInvoiceDetailResponse {
    private Long id;
    private Product product;
    private ImportProductInvoice importProductInvoice;
    private Double quantity;
    private Double importPrice;
    private Double totalPrice;
}
