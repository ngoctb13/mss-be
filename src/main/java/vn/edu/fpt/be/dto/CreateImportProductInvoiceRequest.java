package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateImportProductInvoiceRequest {
    private Long supplierId;
    private List<ImportProductDetailRequest> productDetails;
    private Double pricePaid;
}
