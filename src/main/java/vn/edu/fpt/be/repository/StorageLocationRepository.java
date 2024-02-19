package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.StorageLocation;

public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {
}
