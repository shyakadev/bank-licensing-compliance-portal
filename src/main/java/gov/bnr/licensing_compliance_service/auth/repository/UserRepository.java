package gov.bnr.licensing_compliance_service.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.bnr.licensing_compliance_service.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
