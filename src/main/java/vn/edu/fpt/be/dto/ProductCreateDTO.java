
package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.StorageLocation;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDTO {
    @NotNull(message = "Product name cannot be null")
    @Size(min = 1, message = "Product name cannot be empty")
    private String productName;

    @NotNull(message = "Unit cannot be null")
    private String unit;

    @Min(value = 0, message = "Retail price cannot be negative")
    private Double retailPrice;

    @Min(value = 0, message = "Import price cannot be negative")
    private Double importPrice;

    private String description;

    private String bag_packing;
}
