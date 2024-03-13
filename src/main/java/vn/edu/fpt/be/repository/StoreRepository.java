package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.dto.StoreDTO;
import vn.edu.fpt.be.model.Store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findStoreById(Long id);
    @Query("select s from Store s " +
            "where (:storeName is null or s.storeName like :storeName) " +
            "and (:address is null or s.address =:address) " +
            "and (:phoneNumber is null  or s.phoneNumber =:phoneNumber) " +
            "and (:status is null or s.status = :status)")
    List<Store> findByCriteria(@Param("storeName") String storeName,
                                  @Param("address") String address,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("status") String status);
}
