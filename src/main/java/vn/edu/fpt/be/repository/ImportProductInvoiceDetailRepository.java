package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.ImportProductInvoiceDetail;
import vn.edu.fpt.be.model.SaleInvoiceDetail;

import java.util.List;

public interface ImportProductInvoiceDetailRepository extends JpaRepository<ImportProductInvoiceDetail, Long> {
    List<ImportProductInvoiceDetail> findByImportProductInvoiceId(Long importProductInvoiceId);;
}
