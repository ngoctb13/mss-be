package vn.edu.fpt.be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bag_type")
public class BagType extends Model {
    @Column(name = "weight")
    private Double weight;
    @Column(name = "unit")
    private String unit;
    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
//    @ManyToOne
//    @JoinColumn(name = "store_id")
//    private Store store;
    @ManyToMany(mappedBy = "bagTypes")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();
}
