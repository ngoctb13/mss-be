package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponse {
    private Long id;
    private LocalDateTime createdAt;
    private String createdBy;
    private String storeName;
    private String address;
    private String phoneNumber;
    private User owner;
    private Status status;
}
