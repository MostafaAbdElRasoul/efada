package com.efada.ServiceImpl;

import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.exception.EfadaValidationException;
import com.efada.repository.AppUserRepository;
import com.efada.serviceImpl.AppUserServiceImpl;
import com.efada.utils.EfadaLogger;
import com.efada.utils.FileSystemUtils;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    private AppUserServiceImpl appUserService;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EfadaLogger efadaLogger;

    @Mock
    private FileSystemUtils fileSystemUtils;

    private AppUser testUser;
    private AppUserDTO testUserDTO;

    @BeforeEach
    void setup() throws Exception {
        appUserService = new AppUserServiceImpl(appUserRepository, entityManager, efadaLogger, fileSystemUtils);

        // Inject baseRepository and entityManager via reflection to parent class
        Field repoField = appUserService.getClass().getSuperclass().getDeclaredField("baseRepository");
        repoField.setAccessible(true);
        repoField.set(appUserService, appUserRepository);

        Field emField = appUserService.getClass().getSuperclass().getDeclaredField("entityManager");
        emField.setAccessible(true);
        emField.set(appUserService, entityManager);

        // Common mock setup
        testUser = new AppUser();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password");
        testUser.setProfilePictureName("old.jpg");

        testUserDTO = new AppUserDTO();
        testUserDTO.setUsername("john_doe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setPassword("password");
    }

    @Test
    void save_shouldPersistUser_whenUniqueEmailAndUsername() {
        when(appUserRepository.existsByEmailOrUsername(anyString(), anyString())).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> {
            AppUser u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(entityManager.merge(any())).thenAnswer(inv -> inv.getArgument(0));

        AppUserDTO saved = appUserService.save(testUserDTO);
        assertNotNull(saved);
    }

    @Test
    void save_shouldThrow_whenEmailOrUsernameExists() {
        when(appUserRepository.existsByEmailOrUsername(anyString(), anyString())).thenReturn(true);
        assertThrows(EfadaValidationException.class, () -> appUserService.save(testUserDTO));
    }

    @Test
    void changeUserProfileImage_shouldSucceed_whenUserExists() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "fake image".getBytes());

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fileSystemUtils.getFileExtension(file)).thenReturn(".jpg");
        doNothing().when(fileSystemUtils).deleteFile(any());
        doNothing().when(fileSystemUtils).saveToFileSystem(any(), anyString());
        when(appUserRepository.save(any())).thenReturn(testUser);
        when(fileSystemUtils.getFileBytes(anyString())).thenReturn("image data".getBytes());

        byte[] result = appUserService.changeUserProfileImgae(file, 1L);
        assertNotNull(result);
    }

    @Test
    void changeUserProfileImage_shouldThrow_whenUserNotFound() {
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "fake image".getBytes());
        when(appUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> appUserService.changeUserProfileImgae(file, 99L));
    }

    @Test
    void getUserProfileImage_shouldSucceed_whenUserExists() throws IOException {
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fileSystemUtils.getFileBytes("old.jpg")).thenReturn("image data".getBytes());

        byte[] result = appUserService.getUserProfileImgae(1L);
        assertNotNull(result);
        assertEquals("image data", new String(result));
    }

    @Test
    void getUserProfileImage_shouldThrow_whenUserNotFound() {
        when(appUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> appUserService.getUserProfileImgae(99L));
    }
}
