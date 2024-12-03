package com.nttdata.application.service;

import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticatorServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticatorService authenticatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String username = "john_doe";
        User mockUser = new User();
        mockUser.setLogin(username);
        mockUser.setPassword("password123");

        when(userRepository.findByLogin(username)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = authenticatorService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        verify(userRepository).findByLogin(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String username = "unknown_user";
        when(userRepository.findByLogin(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> authenticatorService.loadUserByUsername(username));

        assertEquals("User not found: " + username, exception.getMessage());
        verify(userRepository).findByLogin(username);
    }
}
