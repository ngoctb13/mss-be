package vn.edu.fpt.be.model;

import javax.persistence.*;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;


@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at", columnDefinition = "DATETIME(0)")
    private LocalDateTime updatedAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

//    @Column(name = "is_delete")
//    private Boolean isDelete;
//
//    @Column(name = "deleted_at")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date deletedAt;
//
//    @Column(name = "deleted_by")
//    private String deletedBy;

}
