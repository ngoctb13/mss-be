
package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDebtDetailRequest {
    @NotNull(message = "Product ID cannot be null")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private Double quantity;

    @Min(value = 0, message = "Distance cannot be negative")
    private Double distance;

    @Min(value = 0, message = "Unit price per distance cannot be negative")
    private Double unitPricePerDistance;

    private Long zoneId;
}
