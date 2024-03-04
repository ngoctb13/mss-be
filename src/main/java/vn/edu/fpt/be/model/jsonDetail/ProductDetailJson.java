package vn.edu.fpt.be.model.jsonDetail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.StorageLocation;

import javax.persistence.Entity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailJson {
    private Long id;
    private String productName;
    private String unit;
    private Double retailPrice;
    private Double importPrice;
    private Double inventory;
    private String bag_packing;
    private StorageLocation storageLocation;
}
