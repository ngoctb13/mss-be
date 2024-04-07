package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.be.model.Store;
import vn.edu.fpt.be.model.User;
import vn.edu.fpt.be.model.enums.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByUsername(String username);
   boolean existsByUsername(String username);
   List<User> findByStoreId(Long storeId);
   List<User> findByStoreIdAndRole (Long storeId, Role role);
   List<User> findByRoleIn(Collection<Role> roles);
   @Query("SELECT u FROM User u WHERE u.store.id = :storeId AND u.role = :role")
   Optional<User> findStoreOwnerByStoreId(@Param("storeId") Long storeId, @Param("role") Role role);

}
