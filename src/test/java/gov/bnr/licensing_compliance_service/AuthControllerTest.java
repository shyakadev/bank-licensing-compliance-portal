package gov.bnr.licensing_compliance_service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.bnr.licensing_compliance_service.auth.RefreshTokenService;
import gov.bnr.licensing_compliance_service.auth.dto.LoginRequest;
import gov.bnr.licensing_compliance_service.auth.entity.RefreshToken;
import gov.bnr.licensing_compliance_service.auth.entity.User;
import gov.bnr.licensing_compliance_service.auth.enums.UserRole;
import gov.bnr.licensing_compliance_service.auth.enums.UserType;
import gov.bnr.licensing_compliance_service.auth.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestcontainersConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername("test-reviewer");
        user.setEmail("testreviewer@bnr.rw");
        user.setFullName("test reviewer");
        user.setUserType(UserType.STAFF);
        user.setPasswordHash(new BCryptPasswordEncoder().encode("password"));
        user.setRole(UserRole.REVIEWER);
        user.setActive(true);

        userRepository.save(user);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("test-reviewer", "password");

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(header().exists(HttpHeaders.SET_COOKIE)) // Check for cookie
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.user").exists()) // Matches controller body
            .andExpect(jsonPath("$.refreshToken").doesNotExist()); // Verification
    }

    @Test
    void shouldRotateTokensSuccessfully() throws Exception {
        User user = userRepository.findByUsername("test-reviewer").orElseThrow();
        RefreshToken existingToken = refreshTokenService.create(user);

        mockMvc.perform(post("/auth/refresh")
                .cookie(new Cookie("refreshToken", existingToken.getTokenHash())))
            .andExpect(status().isOk())
            .andExpect(header().exists(HttpHeaders.SET_COOKIE))
            .andExpect(jsonPath("$.accessToken").exists());
    }

}
