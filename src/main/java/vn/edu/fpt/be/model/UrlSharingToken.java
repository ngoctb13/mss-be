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
@Table(name = "url_sharing_token")
public class UrlSharingToken extends Model {
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "expire_time",nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime expireTime;
}
