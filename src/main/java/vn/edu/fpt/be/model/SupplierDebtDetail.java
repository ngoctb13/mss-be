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
public class SupplierDebtDetail extends Model {
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id", nullable = false)
    private Supplier supplier;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "distance")
    private Double distance;
    @Column(name = "unit_price")//đơn gía
    private Double unitPrice;
    @Column(name = "total_price") // tong tien phai tra cho quang duong = (distance + don gia tren 1 tan) * quantity
    private Double totalPrice;
}
