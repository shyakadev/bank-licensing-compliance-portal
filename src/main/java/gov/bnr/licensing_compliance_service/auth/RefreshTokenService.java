package gov.bnr.licensing_compliance_service.auth;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.bnr.licensing_compliance_service.auth.entity.RefreshToken;
import gov.bnr.licensing_compliance_service.auth.entity.User;
import gov.bnr.licensing_compliance_service.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    public RefreshToken create(User user) {

        RefreshToken token = RefreshToken.builder()
            .tokenHash(UUID.randomUUID()
                .toString())
            .user(user)
            .expiresAt(Instant.now()
                .plusMillis(refreshExpiration))
            .isRevoked(false)
            .build();

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verify(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(token)
            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        if (refreshToken.getExpiresAt()
            .isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
