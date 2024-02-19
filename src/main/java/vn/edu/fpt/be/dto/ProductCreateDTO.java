package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.StorageLocation;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {
    private String productName;
    private String unit;
    private Double retailPrice;
    private Double wholeSalePrice;
    private Double importPrice;
    private Double inventory;
}