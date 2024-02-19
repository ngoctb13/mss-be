package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {



    List<Product> findByStoreId(Long storeId);
}
