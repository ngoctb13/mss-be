package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
