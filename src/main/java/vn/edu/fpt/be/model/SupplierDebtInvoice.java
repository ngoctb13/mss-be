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
public class SupplierDebtInvoice extends Model{
    @Column(name = "total_price") //tong tien cua hoa don
    private Double totalPrice;
    @Column(name = "old_debt") //no cu
    private Double oldDebt;
    @Column(name = "total_payment") //tong tien phai tra = tong tien cua hoa don + no cu
    private Double totalPayment;
    @Column(name = "amount_paid") // so tien da tra
    private Double pricePaid;
    @Column(name = "new_debt") //no con lai = tong tien phai tra - so tien da tra
    private Double newDebt;
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
