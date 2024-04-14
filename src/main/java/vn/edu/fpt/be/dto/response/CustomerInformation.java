package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInformation {
    private Long id;
    private String customerName;
    private String phoneNumber;
    private String address;
    private String note;
    private Double totalDebt;
    private Store store;
    private Status status;
}
