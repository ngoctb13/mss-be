package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationForProductRequest {
    private Long productId;
    private Double inventory;
    private Long storageLocationId;
}
