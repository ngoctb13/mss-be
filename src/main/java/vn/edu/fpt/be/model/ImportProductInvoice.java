package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.fpt.be.dto.SupplierUpdateRequest;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "import_product_invoice")
public class ImportProductInvoice extends Model{
    @Column(name = "total_invoice_price") //tong tien cua hoa don
    private Double totalInvoicePrice;
    @Column(name = "old_debt") //công nợ cũ của chủ cửa hàng
    private Double oldDebt;
    @Column(name = "total_payment") //tong tien phai tra = tong tien cua hoa don + no cu
    private Double totalPayment;
    @Column(name = "price_paid") // so tien da tra
    private Double pricePaid;
    @Column(name = "new_debt") //no con lai = tong tien phai tra - so tien da tra
    private Double newDebt;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
