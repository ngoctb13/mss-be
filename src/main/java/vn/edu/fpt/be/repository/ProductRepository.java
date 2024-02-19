package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
