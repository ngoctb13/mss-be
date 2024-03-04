
package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleInvoiceDetailRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Double quantity;

    @Min(value = 0, message = "Unit price cannot be negative")
    private Double unitPrice;
}
