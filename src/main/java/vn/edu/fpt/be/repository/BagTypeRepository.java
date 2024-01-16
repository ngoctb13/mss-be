package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.be.model.BagType;

@Repository
public interface BagTypeRepository extends JpaRepository<BagType, Long> {
}
