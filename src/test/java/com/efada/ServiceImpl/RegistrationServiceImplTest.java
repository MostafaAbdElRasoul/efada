package com.efada.ServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.efada.dto.CreateRegistrationDTO;
import com.efada.dto.RegistrationDTO;
import com.efada.entity.AppUser;
import com.efada.entity.Registration;
import com.efada.entity.Session;
import com.efada.repository.AppUserRepository;
import com.efada.repository.RegistrationRepository;
import com.efada.repository.SessionRepository;
import com.efada.serviceImpl.RegistrationServiceImpl;
import com.efada.utils.ObjectMapperUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceImplTest {

	private RegistrationServiceImpl registrationService;
	
	@Mock
	private AppUserRepository appUserRepository;
	@Mock
    private SessionRepository sessionRepository;
	@Mock
    private RegistrationRepository registrationRepository;
	@Mock
    private EntityManager entityManager;
	
	private AppUser mockUser;
    private Session mockSession;
    private Registration mockRegistration;
    private CreateRegistrationDTO dto;

    @BeforeEach
    void setup() throws Exception {
        registrationService = new RegistrationServiceImpl(
                appUserRepository,
                sessionRepository,
                registrationRepository
        );

        // Inject mocks via reflection into BaseServiceImpl fields
        var repoField = registrationService.getClass().getSuperclass().getDeclaredField("baseRepository");
        repoField.setAccessible(true);
        repoField.set(registrationService, registrationRepository);

        var emField = registrationService.getClass().getSuperclass().getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(registrationService, entityManager);

        // Test data
        mockUser = new AppUser();
        mockUser.setId(1L);

        mockSession = new Session();
        mockSession.setId(2L);

        mockRegistration = new Registration();
        mockRegistration.setId(100L);
        mockRegistration.setAttendee(mockUser);
        mockRegistration.setSession(mockSession);
        mockRegistration.setRegisteredAt(Instant.now());

        dto = new CreateRegistrationDTO();
        dto.setAttendeeId(1L);
        dto.setSessionId(2L);
    }

    @Test
    void insert_shouldSucceed_whenUserAndSessionExist() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(sessionRepository.findById(2L)).thenReturn(Optional.of(mockSession));
        when(registrationRepository.save(any(Registration.class))).thenReturn(mockRegistration);

        try (MockedStatic<ObjectMapperUtils> mocked = mockStatic(ObjectMapperUtils.class)) {
            RegistrationDTO expected = new RegistrationDTO();
            expected.setId(100L);
            mocked.when(() -> ObjectMapperUtils.map(any(Registration.class), eq(RegistrationDTO.class)))
                  .thenReturn(expected);

            RegistrationDTO result = registrationService.insert(dto);

            assertNotNull(result);
            assertEquals(100L, result.getId());
        }
    }

    @Test
    void insert_shouldThrow_whenUserNotFound() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> registrationService.insert(dto));
        assertEquals("USER_NOT_FOUND", ex.getMessage());
    }

    @Test
    void insert_shouldThrow_whenSessionNotFound() {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(sessionRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> registrationService.insert(dto));
        assertEquals("SESSION_NOT_FOUND", ex.getMessage());
    }
}
