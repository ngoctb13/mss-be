package vn.edu.fpt.be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Gender;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String email;
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String identityNumber;
}
