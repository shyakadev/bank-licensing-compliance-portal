package gov.bnr.licensing_compliance_service.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.bnr.licensing_compliance_service.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String token);
}
