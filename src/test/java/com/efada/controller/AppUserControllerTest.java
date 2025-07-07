package com.efada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.efada.entity.AppUser;
import com.efada.enums.UserRole;
import com.efada.repository.AppUserRepository;
import com.efada.security.EfadaSecurityUser;
import com.efada.security.jwt.JwtService;

import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
    private JwtService jwtService;

    private Long testUserId;
    private String jwtToken;
    
    @BeforeEach
    void setUp() {
    	AppUser user = new AppUser();
        user.setUsername("test_user");
        user.setEmail("test.user@example.com");
        user.setPassword("secure123");
        user.setRole(UserRole.ADMIN); // or whatever role is needed

        testUserId = appUserRepository.save(user).getId();
        EfadaSecurityUser authUser = new EfadaSecurityUser(
            user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ADMIN")),
            user.getRole().toString(), user.getId()
        );
        jwtToken = jwtService.generateAccessToken(authUser);
    }

    @Test
    void changeUserProfileImage_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-data".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/users/{id}/image", testUserId)
                        .file(file)
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .header("Authorization", "Bearer " + jwtToken))
        
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void getUserProfileImage_shouldReturn200AndImageBytes() throws Exception {
        // First upload
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-data".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/users/{id}/image", testUserId)
                .file(file)
                .with(req -> { req.setMethod("PUT"); return req; })
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk());

        // Then get with JWT
        EfadaSecurityUser efadaUser = new EfadaSecurityUser(
            "test_user", "secure123",
            List.of(() -> "ADMIN"), "ADMIN", testUserId
        );

        mockMvc.perform(get("/api/v1/users/{id}/image", testUserId)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isNotEmpty());
    }


    @Test
    void getUserProfileImage_shouldReturnError_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/image", 9999L)
        		.header("Authorization", "Bearer " + jwtToken))
        
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
