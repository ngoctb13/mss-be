package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forgot_password_token")
public class ForgotPasswordToken extends Model{
    @Column(nullable = false)
    private String token;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime expireTime;
    @Column(nullable = false)
    private boolean isUsed;
}
