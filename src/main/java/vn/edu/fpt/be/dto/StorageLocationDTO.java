package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocationDTO {
    private Long id;
    private String locationName;
    private String description;
    private Product product;
    private Status status;
}
