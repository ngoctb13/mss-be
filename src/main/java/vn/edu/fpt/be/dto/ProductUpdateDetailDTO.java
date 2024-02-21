package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.StorageLocation;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateDetailDTO {
    private String productName;
    private String unit;
    private Double retailPrice;
    private Double wholeSalePrice;
    private Double importPrice;
    private Double begin_inventory;
    private Double inventory;
    private StorageLocation storageLocation;
}
