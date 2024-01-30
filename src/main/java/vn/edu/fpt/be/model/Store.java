package vn.edu.fpt.be.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Store extends Model{
    @Column(name = "store_name")
    private String storeName;
    @Column(name = "address")
    private String address;
    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;
    @Column(name = "status", columnDefinition = "VARCHAR(30) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
}
