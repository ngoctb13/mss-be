package vn.edu.fpt.be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.model.enums.Status;


import java.util.HashSet;
import java.util.Set;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;
    @Column(name = "unit", length = 50)
    private String unit;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_bag_type",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<BagType> bagTypes = new HashSet<>();
    @Column(name = "retail_price")
    private Double retailPrice;
    @Column(name = "wholesale_price")
    private Double wholesalePrice;
    @Column(name = "import_price")
    private Double importPrice;
    @Column(name = "description")
    private String description;
    @Column(name = "status", columnDefinition = "Active")
    private Status status;

}
