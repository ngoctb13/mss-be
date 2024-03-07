package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.SaleInvoice;

import java.util.List;

public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Long> {
    List<SaleInvoice> findByCustomerIdAndStoreIdOrderByCreatedAtDesc(Long customerId, Long storeId);
}
