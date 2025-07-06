package com.efada.ServiceImpl;

import com.efada.base.BaseResponse;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.redis.repositories.LoggedUserRepository;
import com.efada.repository.AppUserRepository;
import com.efada.repository.UserLoginHistoryRepository;
import com.efada.security.jwt.JwtService;
import com.efada.serviceImpl.AuthenticationService;
import com.efada.utils.EfadaUtils;
import com.efada.utils.EmailService;
import com.efada.utils.OTPUtils;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.MockedStatic;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    AuthenticationService authService;

    @Mock EfadaUtils efadaUtils;
    @Mock AppUserRepository appUserRepository;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock LoggedUserRepository loggedUserRepository;
    @Mock HttpServletRequest request;
    @Mock UserLoginHistoryRepository userLoginHistoryRepository;
    @Mock OTPUtils otpUtils;
    @Mock EmailService emailService;
    @Mock PasswordEncoder passwordEncoder;

    AppUser user;

    @BeforeEach
    void setUp() {
        authService = new AuthenticationService(
            efadaUtils, appUserRepository, authenticationManager, jwtService,
            loggedUserRepository, request, userLoginHistoryRepository,
            otpUtils, emailService, passwordEncoder
        );

        user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(UserRole.ATTENDEE);
    }

    @Test
    void login_shouldSucceed_whenCredentialsValid() {
        ObjectNode loginNode = new ObjectMapper().createObjectNode();
        loginNode.put("username", "testuser");
        loginNode.put("password", "123456");

        when(appUserRepository.findByUsernameIgnoreCase("testuser"))
            .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any())).thenReturn(null);

        try (MockedStatic<ObjectMapperUtils> mockStatic = mockStatic(ObjectMapperUtils.class)) {
            AppUserDTO dto = new AppUserDTO();
            dto.setId(1L);
            dto.setUsername("testuser");
            dto.setRole(UserRole.ATTENDEE);
            dto.setPassword("123456");
            mockStatic.when(() -> ObjectMapperUtils.map(user, AppUserDTO.class))
                      .thenReturn(dto);

            when(jwtService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

            BaseResponse response = authService.login(loginNode);

            assertNotNull(response);
            assertEquals(1L, ((AppUserDTO) response.getData()).getId());
        }
    }

    @Test
    void refreshToken_shouldReturnNewTokens_whenValid() {
        when(jwtService.isValidRefreshToken(anyString())).thenReturn(true);
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");
        when(appUserRepository.findByUsernameIgnoreCase("testuser"))
            .thenReturn(Optional.of(user));

        try (MockedStatic<ObjectMapperUtils> mockStatic = mockStatic(ObjectMapperUtils.class)) {
            AppUserDTO dto = new AppUserDTO();
            dto.setId(1L);
            dto.setUsername("testuser");
            dto.setRole(UserRole.ATTENDEE);
            dto.setPassword("encodedPassword");
            mockStatic.when(() -> ObjectMapperUtils.map(user, AppUserDTO.class))
                      .thenReturn(dto);

            when(jwtService.generateAccessToken(any())).thenReturn("new-access");
            when(jwtService.generateRefreshToken(any())).thenReturn("new-refresh");

            BaseResponse response = authService.refreshToken("Bearer valid-token");

            assertTrue(response.getStatus());
            assertEquals("new-access", response.getTokens().get("accessToken"));
        }
    }

    @Test
    void forgotPassword_shouldSendOtp_whenEmailExists() {
        when(appUserRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(user));

        when(otpUtils.generateOTP("test@example.com")).thenReturn(123456);
        when(efadaUtils.getMessageFromMessageSource(anyString(), any(), any())).thenReturn("message");

        ResponseEntity<BaseResponse> response = authService.forgotPassword("test@example.com");
        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void resetPassword_shouldUpdatePassword_whenOtpValid() {
        when(otpUtils.verifyOTP("test@example.com", 123456)).thenReturn(true);
        when(appUserRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newpass")).thenReturn("encodedPass");

        when(efadaUtils.getMessageFromMessageSource(anyString(), any(), any())).thenReturn("reset ok");

        ResponseEntity<BaseResponse> response = authService.resetPassword("test@example.com", 123456, "newpass");
        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void resetPassword_shouldFail_whenOtpInvalid() {
        when(otpUtils.verifyOTP("test@example.com", 999999)).thenReturn(false);
        when(efadaUtils.getMessageFromMessageSource(anyString(), any(), any())).thenReturn("Invalid OTP");

        ResponseEntity<BaseResponse> response = authService.resetPassword("test@example.com", 999999, "newpass");
        assertEquals(400, response.getBody().getCode());
    }
}
