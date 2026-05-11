package gov.bnr.licensing_compliance_service.auth.dto;

import gov.bnr.licensing_compliance_service.auth.enums.UserRole;

public record UserResponse(Long id, String username, String email, String fullName, UserRole role) {

}
