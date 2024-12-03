package com.nttdata.application.service;

import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_UserFound() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.findById(1L);
        });

        assertEquals("User not found with id: 1", thrown.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John Doe");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");

        Page<User> usersPage = new PageImpl<>(Arrays.asList(user1, user2), PageRequest.of(0, 2), 2);

        when(userRepository.findAll(PageRequest.of(0, 2))).thenReturn(usersPage);

        Page<User> result = userService.findAll(PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(userRepository, times(1)).findAll(PageRequest.of(0, 2));
    }

    @Test
    void testGetUserWithDetails_UserFound() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        when(userRepository.findByIdWithAccountsAndTransactions(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserWithDetails(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findByIdWithAccountsAndTransactions(1L);
    }

    @Test
    void testGetUserWithDetails_UserNotFound() {
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserWithDetails(1L);
        });

        assertEquals("User not found", thrown.getMessage());
        verify(userRepository, times(1)).findByIdWithAccountsAndTransactions(1L);
    }
}


