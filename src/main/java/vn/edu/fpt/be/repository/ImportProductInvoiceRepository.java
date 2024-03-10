package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.ImportProductInvoice;
import vn.edu.fpt.be.model.SaleInvoice;

import java.util.List;

public interface ImportProductInvoiceRepository extends JpaRepository<ImportProductInvoice, Long> {
    List<ImportProductInvoice> findBySupplierIdAndStoreIdOrderByCreatedAtDesc(Long supplierId, Long storeId);
}
