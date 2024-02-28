package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.Supplier;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDetailDTO {
    private Long id;
    private Product product;
    private Supplier supplier;
    private Double quantity;
    private Double distance;
    private Double unitPrice;
    private Double totalPrice;
}
