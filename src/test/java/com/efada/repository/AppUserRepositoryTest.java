package com.efada.repository;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.efada.entity.AppUser;

@DataJpaTest
@DisplayName("AppUser Repository Tests")
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser1;
    private AppUser testUser2;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new AppUser();
        testUser1.setUsername("john_doe");
        testUser1.setEmail("john.doe@example.com");
        testUser1.setPassword("password123");
        // Set other required fields based on your AppUser entity
        
        testUser2 = new AppUser();
        testUser2.setUsername("jane_smith");
        testUser2.setEmail("jane.smith@example.com");
        testUser2.setPassword("password456");
        
        testUser1 = appUserRepository.save(testUser1);
        testUser2 = appUserRepository.save(testUser2);

    }
    
    @AfterEach
    void resetInsertedData() {
    	appUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find user by username ignoring case")
    void findByUsernameIgnoreCase_ShouldReturnUser_WhenUsernameExistsIgnoringCase() {
        // When
        Optional<AppUser> foundUser = appUserRepository.findByUsernameIgnoreCase("JOHN_DOE");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("john_doe", foundUser.get().getUsername());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when username does not exist")
    void findByUsernameIgnoreCase_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        // When
        Optional<AppUser> foundUser = appUserRepository.findByUsernameIgnoreCase("nonexistent");

        // Then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should find user by email ignoring case")
    void findByEmailIgnoreCase_ShouldReturnUser_WhenEmailExistsIgnoringCase() {
        // When
        Optional<AppUser> foundUser = appUserRepository.findByEmailIgnoreCase("JOHN.DOE@EXAMPLE.COM");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
        assertEquals("john_doe", foundUser.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void findByEmailIgnoreCase_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // When
        Optional<AppUser> foundUser = appUserRepository.findByEmailIgnoreCase("nonexistent@example.com");

        // Then
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("Should return true when email exists ignoring case")
    void existsByEmailIgnoreCase_ShouldReturnTrue_WhenEmailExistsIgnoringCase() {
        // When
        boolean exists = appUserRepository.existsByEmailIgnoreCase("JOHN.DOE@EXAMPLE.COM");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmailIgnoreCase_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // When
        boolean exists = appUserRepository.existsByEmailIgnoreCase("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return true when email exists in existsByEmailOrUsername")
    void existsByEmailOrUsername_ShouldReturnTrue_WhenEmailExists() {
        // When
        boolean exists = appUserRepository.existsByEmailOrUsername("john.doe@example.com", "someusername");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return true when username exists in existsByEmailOrUsername")
    void existsByEmailOrUsername_ShouldReturnTrue_WhenUsernameExists() {
        // When
        boolean exists = appUserRepository.existsByEmailOrUsername("some@email.com", "john_doe");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return true when both email and username exist in existsByEmailOrUsername")
    void existsByEmailOrUsername_ShouldReturnTrue_WhenBothEmailAndUsernameExist() {
        // When
        boolean exists = appUserRepository.existsByEmailOrUsername("john.doe@example.com", "jane_smith");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when neither email nor username exist in existsByEmailOrUsername")
    void existsByEmailOrUsername_ShouldReturnFalse_WhenNeitherEmailNorUsernameExist() {
        // When
        boolean exists = appUserRepository.existsByEmailOrUsername("nonexistent@example.com", "nonexistentuser");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should save and retrieve user successfully")
    void save_ShouldPersistUser_WhenValidUserProvided() {
        // Given
        AppUser newUser = new AppUser();
        newUser.setUsername("new_user");
        newUser.setEmail("new.user@example.com");
        newUser.setPassword("newpassword");
        // Set other required fields

        // When
        AppUser savedUser = appUserRepository.save(newUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("new_user", savedUser.getUsername());
        assertEquals("new.user@example.com", savedUser.getEmail());

        // Verify it can be found
        Optional<AppUser> foundUser = appUserRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("new_user", foundUser.get().getUsername());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void delete_ShouldRemoveUser_WhenUserExists() {
        // Given
        Long userId = testUser1.getId();

        // When
        appUserRepository.delete(testUser1);

        // Then
        Optional<AppUser> deletedUser = appUserRepository.findById(userId);
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    @DisplayName("Should find all users")
    void findAll_ShouldReturnAllUsers() {
        // When
        List<AppUser> allUsers = (List<AppUser>) appUserRepository.findAll();

        // Then
        assertNotEquals(0, allUsers.size());
        
        boolean hasJohnDoe = allUsers.stream().anyMatch(user -> "john_doe".equals(user.getUsername()));
        boolean hasJaneSmith = allUsers.stream().anyMatch(user -> "jane_smith".equals(user.getUsername()));
        
        assertTrue(hasJohnDoe);
        assertTrue(hasJaneSmith);
    }

    @Test
    @DisplayName("Should count users correctly")
    void count_ShouldReturnCorrectCount() {
        // When
        long count = appUserRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if user exists by ID")
    void existsById_ShouldReturnTrue_WhenUserExists() {
        // When
        boolean exists = appUserRepository.existsById(testUser1.getId());

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when checking non-existent user ID")
    void existsById_ShouldReturnFalse_WhenUserDoesNotExist() {
        // When
        boolean exists = appUserRepository.existsById(-1L);

        // Then
        assertFalse(exists);
    }
}