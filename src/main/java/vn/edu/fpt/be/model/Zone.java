package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends Model{
    @Column(name = "zone_name")
    private String zoneName;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn (name = "store_id"  , referencedColumnName = "id", nullable = false)
    private Store store;
    @OneToOne
    @JoinColumn (name = "product_id" ,referencedColumnName = "id", nullable = false)
    private Product product;
}
