package vn.edu.fpt.be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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
    @ManyToMany(mappedBy = "bagTypes")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();
}
