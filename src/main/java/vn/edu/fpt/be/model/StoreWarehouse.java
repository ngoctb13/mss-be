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
@Table(name = "store_warehouse")
public class StoreWarehouse extends Model {
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "warehouse_name")
    private String warehouseName;
    @Column(name = "description")
    private String description;
    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
    private Status status;
}
