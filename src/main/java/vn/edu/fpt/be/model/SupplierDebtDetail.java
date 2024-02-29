package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDebtDetail  extends Model{
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "supplierId", referencedColumnName = "id", nullable = false)
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "zoneId", referencedColumnName = "id", nullable = false)
    private Zone zone;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "distance") //khoảng cách
    private Double distance;
    @Column(name = "unit_price_per_distance") //đơn giá trên khoảng cách
    private Double unitPricePerDistance;
    @Column(name = "total_price") // tong tien cua mot đơn v = distance * unit_price_per_distance  *quanlity
    private Double totalPrice;
}
