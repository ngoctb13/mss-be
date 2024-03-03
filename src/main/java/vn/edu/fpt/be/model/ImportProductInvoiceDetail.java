package vn.edu.fpt.be.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "import_product_invoice_detail")
public class ImportProductInvoiceDetail extends Model {
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "import_product_invoice_id", referencedColumnName = "id", nullable = false)
    private ImportProductInvoice importProductInvoice;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "importPrice") //đơn gía
    private Double importPrice;
    @Column(name = "total_price") // tong tien cua mot san pham = quantity * importPrice
    private Double totalPrice;
    @ManyToOne
    @JoinColumn(name = "storage_location_id", referencedColumnName = "id", nullable = false)
    private StorageLocation storageLocation;
    @Column(name = "product_details_at_time_of_import", columnDefinition = "json")
    private String productDetailsAtTimeOfImport;
}
