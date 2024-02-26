package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.SaleInvoice;

public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Long> {
}
