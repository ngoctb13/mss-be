
package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private Long id;

//    @NotNull(message = "Customer name cannot be null")
//    @Size(min = 1, message = "Customer name cannot be empty")
    private String customerName;

//    @NotNull(message = "Phone number cannot be null")
//    @Pattern(regexp = "[0-9]{10,15}", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

//    @Size(min = 1, message = "Address cannot be empty")
    private String address;

    private String note;

    private Double totalDebt;
    @NotNull(message = "Status cannot be null")
    private Status status;
}
