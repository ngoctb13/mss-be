package vn.edu.fpt.be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.Store;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDebtResponse {
    private Long id;
    private LocalDateTime createdAt;
    private String creditorName;
    private String creditorPhone;
    private String creditorAddress;
    private Double amount;
    private String note;
    private Store store;
}
