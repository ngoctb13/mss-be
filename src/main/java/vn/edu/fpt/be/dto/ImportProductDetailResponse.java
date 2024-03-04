package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.StorageLocation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportProductDetailResponse {
    private Long id;
    private Product importedProduct;
    private Double quantity;
    private Double importPrice;
    private Double totalPrice;
    private StorageLocation storageLocation;
//    private String productDetailsAtTimeOfImport;
}
