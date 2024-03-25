package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.be.model.StorageLocation;

import java.util.List;

public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    List<StorageLocation> findByStoreId(Long storeId);
    @Query("select sl, p from StorageLocation sl, Product p where sl.product.id= p.id")
    List<StorageLocation> findStorageLocationByStoreId(Long storeId);
    List<StorageLocation> findByProductId(Long id);
}
