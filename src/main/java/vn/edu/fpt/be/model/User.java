package vn.edu.fpt.be.model;


import javax.persistence.*;
import lombok.*;
import vn.edu.fpt.be.model.enums.Role;
import vn.edu.fpt.be.model.enums.Status;


@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends Model {
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
}
