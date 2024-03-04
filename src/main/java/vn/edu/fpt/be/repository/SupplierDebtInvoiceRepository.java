package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.SupplierDebtDetail;
import vn.edu.fpt.be.model.SupplierDebtInvoice;

public interface SupplierDebtInvoiceRepository extends JpaRepository<SupplierDebtInvoice, Long> {
}
