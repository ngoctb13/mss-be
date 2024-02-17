package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "storage_location")
public class StorageLocation extends Model {
    @Column(name = "location_name")
    private String locationName;
    @Column(name = "capacity")
    private int capacity;
    @Column(name = "description")
    private String description;
    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
