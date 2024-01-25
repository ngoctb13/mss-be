package vn.edu.fpt.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreAddDTO {
    private String storeName;
    private String address;
    private User owner;
    private Status status;
}