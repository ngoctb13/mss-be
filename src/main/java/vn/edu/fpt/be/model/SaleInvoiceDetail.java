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
public class SaleInvoiceDetail extends Model {
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "sale_invoice_id", referencedColumnName = "id", nullable = false)
    private SaleInvoice saleInvoice;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "unit_price") //đơn gía
    private Double unitPrice;
    @Column(name = "total_price") // tong tien cua mot san pham = quantity * unit price
    private Double totalPrice;
    @Column(name = "product_details_at_time_of_import", columnDefinition = "json")
    private String productDetailsAtTimeOfImport;
}
