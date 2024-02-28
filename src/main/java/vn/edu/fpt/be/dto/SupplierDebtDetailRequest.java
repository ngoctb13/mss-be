package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDebtDetailRequest {
    private Long productId;
    private Double quantity;
    private Double distance;
    private Long zoneId;
}
