package vn.edu.fpt.be.model;

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
@Table(name = "product")
public class Product extends Model {
    @Column(name = "product_name", unique = true, nullable = false)
    private String productName;
    @Column(name = "unit", length = 50)
    private String unit;
    @Column(name = "retail_price") // gia le
    private Double retailPrice;
    @Column(name = "wholesale_price") //gia si
    private Double wholesalePrice;
    @Column(name = "import_price") //gia nhap
    private Double importPrice;
    @Column(name = "description")
    private String description;
    @Column(name = "begin_inventory") //tồn đầu
    private Double begin_inventory;
    @Column(name = "inventory") //còn tồn
    private Double inventory;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_bag_type",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private Set<BagType> bagTypes = new HashSet<>();
    @Column(name = "status", columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @ManyToOne
    @JoinColumn(name = "storage_location_id")
    private StorageLocation storageLocation;
}
