package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.dto.StorageLocationDTO;
import vn.edu.fpt.be.model.StorageLocation;

import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.SqlResultSetMapping;
import java.util.List;

public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
    List<StorageLocation> findByStoreId(Long storeId);
    @Query("select distinct storage_location.id ,storage_location.locationName from StorageLocation storage_location " +
            "where storage_location.id=:storeId " +
            "group by storage_location.locationName " +
            "order by storage_location.id")
    List<StorageLocation> findStorageLocations(@Param("storeId") Long storeId);
    List<StorageLocation> findByProductId(Long id);
    boolean existsByProductIdAndStoreId(Long productId, Long storeId);
}
