package vn.edu.fpt.be.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bage_type")
public class BagType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Long bagTypeId;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "unit")
    private String unit;
    @ManyToMany(mappedBy = "bagTypes")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

}
