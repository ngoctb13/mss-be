
package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierCreateDTO {
    @NotNull(message = "Supplier name cannot be null")
    @Size(min = 1, message = "Supplier name cannot be empty")
    private String supplierName;

    @Pattern(regexp = "[0-9]{10,15}", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @Size(min = 1, message = "Address cannot be empty")
    private String address;
}
