package gov.bnr.licensing_compliance_service.auth;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.bnr.licensing_compliance_service.auth.dto.AuthResponse;
import gov.bnr.licensing_compliance_service.auth.dto.LoginRequest;
import gov.bnr.licensing_compliance_service.auth.entity.RefreshToken;
import gov.bnr.licensing_compliance_service.auth.entity.User;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

        ResponseCookie cookie = authService.createRefreshTokenCookie(response.refreshToken());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("accessToken", response.accessToken(), "user", response.userResponse()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String token) {

        RefreshToken refreshToken = refreshTokenService.verify(token);

        User user = refreshToken.getUser();

        refreshTokenService.revoke(refreshToken);

        RefreshToken newRefresh = refreshTokenService.create(user);

        String accessToken = authService.generateAccessToken(user);

        ResponseCookie cookie = authService.createRefreshTokenCookie(newRefresh.getTokenHash());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Map.of("accessToken", accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String token) {

        RefreshToken refreshToken = refreshTokenService.verify(token);

        refreshTokenService.revoke(refreshToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authService.clearRefreshCookie()
                .toString())
            .build();
    }
}
