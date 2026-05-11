package gov.bnr.licensing_compliance_service.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, UserResponse userResponse) {

}
