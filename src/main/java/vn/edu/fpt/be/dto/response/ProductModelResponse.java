package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.StorageLocation;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductModelResponse {
    private Long id;
    private String productName;
    private String unit;
    private Double retailPrice;
    private Double importPrice;
    private String description;
    private Double inventory;
    private String bag_packing;
    private Status status;
    private Store store;
    private List<StorageLocation> storageLocations;
}
