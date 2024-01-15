package vn.edu.fpt.be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long userProfileId;
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
