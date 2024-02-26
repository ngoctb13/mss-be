package vn.edu.fpt.be.model;

import javax.persistence.*;
import lombok.*;
import vn.edu.fpt.be.model.enums.Gender;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfile extends Model {
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "identity_number", unique = true)
    private String identityNumber;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
