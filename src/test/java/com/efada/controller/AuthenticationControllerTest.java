package com.efada.controller;

import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.repository.AppUserRepository;
import com.efada.security.EfadaSecurityUser;
import com.efada.security.jwt.JwtService;
import com.efada.utils.OTPUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private OTPUtils otpUtils;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    private AppUser testUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll(); // Ensure no unique constraint conflicts

        testUser = new AppUser();
        testUser.setUsername("integration_user");
        testUser.setEmail("integration@example.com");
        testUser.setPassword(passwordEncoder.encode("password123")); // Encode it
        testUser.setRole(UserRole.ADMIN);

        testUser = appUserRepository.save(testUser);

        EfadaSecurityUser authUser = new EfadaSecurityUser(
                testUser.getUsername(),
                testUser.getPassword(),
                List.of(new SimpleGrantedAuthority("ADMIN")),
                testUser.getRole().toString(),
                testUser.getId()
        );

        jwtToken = jwtService.generateAccessToken(authUser);
    }

    @Test
    void login_shouldReturnTokens_whenCredentialsValid() throws Exception {
        ObjectNode loginData = objectMapper.createObjectNode();
        loginData.put("username", testUser.getUsername());
        loginData.put("password", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.tokens.accessToken").exists())
                .andExpect(jsonPath("$.tokens.refreshToken").exists());
    }

    @Test
    void forgotPassword_shouldSendOtp() throws Exception {
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .param("email", testUser.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void resetPassword_shouldWork_withValidOtp() throws Exception {
        int otp = otpUtils.generateOTP(testUser.getEmail());

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .param("email", testUser.getEmail())
                        .param("otp", String.valueOf(otp))
                        .param("newPassword", "newPassword123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void resetPassword_shouldFail_withInvalidOtp() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .param("email", testUser.getEmail())
                        .param("otp", "999999")
                        .param("newPassword", "newPassword123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void refreshToken_shouldReturnNewAccessToken() throws Exception {
        ObjectNode loginData = objectMapper.createObjectNode();
        loginData.put("username", testUser.getUsername());
        loginData.put("password", "password123");

        // First login to get refresh token
        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = objectMapper.readTree(response)
                .path("tokens")
                .path("refreshToken")
                .asText();
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokens.accessToken").exists());
    }
}
