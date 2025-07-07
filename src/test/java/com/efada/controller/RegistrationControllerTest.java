package com.efada.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.efada.dto.CreateRegistrationDTO;
import com.efada.entity.AppUser;
import com.efada.entity.Session;
import com.efada.enums.UserRole;
import com.efada.repository.AppUserRepository;
import com.efada.repository.SessionRepository;
import com.efada.security.EfadaSecurityUser;
import com.efada.security.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class RegistrationControllerTest {
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;
    private Long sessionId;

    @Autowired
    private JwtService jwtService;
    private String jwtToken;
    
    @BeforeEach
    void setup() {
        // Create and save a test user
        AppUser user = new AppUser();
        user.setUsername("integrationUser");
        user.setEmail("integration@example.com");
        user.setPassword("pass123");
        user.setRole(UserRole.ATTENDEE);
        user = appUserRepository.save(user);
        userId = user.getId();

        // Create and save a session
        Session session = new Session();
        session.setTitle("Integration Test Session");
        session.setDescription("Test Description");
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now());
        session = sessionRepository.save(session);
        sessionId = session.getId();
        
        
        EfadaSecurityUser authUser = new EfadaSecurityUser(
                user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ADMIN")),
                user.getRole().toString(), user.getId()
            );
        jwtToken = jwtService.generateAccessToken(authUser);
    }

    @Test
    void insert_shouldCreateRegistrationSuccessfully() throws Exception {
        CreateRegistrationDTO dto = new CreateRegistrationDTO();
        dto.setAttendeeId(userId);
        dto.setSessionId(sessionId);

        mockMvc.perform(post("/api/v1/registrations/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void insert_shouldReturn400_whenUserOrSessionNotFound() throws Exception {
        CreateRegistrationDTO dto = new CreateRegistrationDTO();
        dto.setAttendeeId(9999L); // Non-existent user
        dto.setSessionId(8888L); // Non-existent session

        mockMvc.perform(post("/api/v1/registrations/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
