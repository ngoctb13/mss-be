package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductLocationResponse {
    private List<ProductWithLocations> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductWithLocations {
        private Long productId;
        private String productName;
        private String unit;
        private Double retailPrice;
        private Double inventory;
        private String bag_packing;
        private Status status;
        private List<LocationInfo> locations;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationInfo {
        private Long locationId;
        private String locationName;
        private String description;
    }
}
