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
    @Column(name = "retail_price") // gia ban
    private Double retailPrice;
    @Column(name = "import_price") //gia nhap
    private Double importPrice;
    @Column(name = "description")
    private String description;
    @Column(name = "inventory") //còn tồn (còn bao nhiêu kg)
    private Double inventory;
    @Column(name = "bag_packing") //quy cách đóng bao
    private String bag_packing;
    @Column(name = "status", columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
