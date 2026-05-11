package gov.bnr.licensing_compliance_service.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import gov.bnr.licensing_compliance_service.auth.dto.AuthResponse;
import gov.bnr.licensing_compliance_service.auth.dto.LoginRequest;
import gov.bnr.licensing_compliance_service.auth.dto.UserResponse;
import gov.bnr.licensing_compliance_service.auth.entity.RefreshToken;
import gov.bnr.licensing_compliance_service.auth.entity.User;
import gov.bnr.licensing_compliance_service.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;

    @Value("${jwt.access-token-expiration}")
    private long accessExpiration;

    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String username = ((org.springframework.security.core.userdetails.User) Objects.requireNonNull(authentication.getPrincipal())).getUsername();
        User user = userRepository.findByUsername(username)
            .orElseThrow();

        String accessToken = generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new AuthResponse(accessToken, refreshToken.getTokenHash(), toDto(user));
    }

    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        return Jwts.builder()
            .subject(user.getEmail())
            .claim("role", user.getRole()
                .name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(accessExpiration)))
            .signWith(key)
            .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {

        return ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/auth/refresh")
            .maxAge(Duration.ofMillis(refreshExpiration))
            .build();
    }

    public ResponseCookie clearRefreshCookie() {

        return ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/auth/refresh")
            .maxAge(0)
            .build();
    }

    public UserResponse toDto(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
