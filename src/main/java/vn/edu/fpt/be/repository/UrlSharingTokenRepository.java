package vn.edu.fpt.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.fpt.be.model.UrlSharingToken;

public interface UrlSharingTokenRepository extends JpaRepository<UrlSharingToken, Long> {
    UrlSharingToken findByToken(String token);
}
