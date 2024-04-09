package vn.edu.fpt.be.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Product;
import vn.edu.fpt.be.model.enums.Status;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(Long storeId);
    List<Product> findByProductName(String productName);
//    List<Product> findByStoreIdAndProductNameContaining(Long storeId, String nameInput);
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.status = :status AND LOWER(p.productName) LIKE LOWER(CONCAT('%',:nameInput,'%'))")
    List<Product> findByStoreIdAndStatusAndProductNameContaining(Long storeId, Status status, String nameInput);
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND (:nameInput IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%',:nameInput,'%')))")
    List<Product> findByStoreIdAndProductNameContaining(@Param("storeId") Long storeId, @Param("nameInput") String nameInput);

}
