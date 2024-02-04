package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {
}
