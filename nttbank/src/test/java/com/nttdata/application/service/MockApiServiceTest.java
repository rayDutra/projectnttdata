package com.nttdata.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MockApiServiceTest {

    @InjectMocks
    private MockApiService mockApiService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${api.mock.url}")
    private String apiUrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        Object[] mockAccounts = new Object[]{"user1", "user2"};
        when(restTemplate.getForObject(apiUrl, Object[].class)).thenReturn(mockAccounts);

        List<Object> users = mockApiService.getUsers();

        assertNotNull(users, "The users list should not be null");
        assertEquals(2, users.size(), "The users list should contain 2 elements");
        assertEquals("user1", users.get(0), "The first user should be 'user1'");
        assertEquals("user2", users.get(1), "The second user should be 'user2'");
    }

    @Test
    public void testGetUsers_EmptyResponse() {
        Object[] mockAccounts = new Object[]{};
        when(restTemplate.getForObject(apiUrl, Object[].class)).thenReturn(mockAccounts);

        List<Object> users = mockApiService.getUsers();

        assertNotNull(users, "The users list should not be null");
        assertTrue(users.isEmpty(), "The users list should be empty");
    }

    @Test
    public void testGetUsers_ApiError() {
        when(restTemplate.getForObject(apiUrl, Object[].class)).thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> mockApiService.getUsers(), "An exception should be thrown if the API is unavailable");
    }

    @Test
    public void testGetUsersById_UserFound() {
        String userId = "123";
        Object mockUser = new Object();
        String url = apiUrl + "/" + userId;

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockUser);

        Object user = mockApiService.getUsersById(userId);

        assertNotNull(user, "The user should not be null");
    }

    @Test
    public void testGetUsersById_UserNotFound() {
        String userId = "999";
        String url = apiUrl + "/" + userId;

        when(restTemplate.getForObject(url, Object.class)).thenReturn(null);

        Object user = mockApiService.getUsersById(userId);

        assertNull(user, "The user should be null if not found");
    }

    @Test
    public void testGetUsersById_ApiError() {
        String userId = "123";
        String url = apiUrl + "/" + userId;

        when(restTemplate.getForObject(url, Object.class)).thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> mockApiService.getUsersById(userId), "An exception should be thrown if the API is unavailable");
    }

    @Test
    public void testGetUsersById_Success() {
        String userId = "123";
        Object mockUser = "user123";
        String url = apiUrl + "/" + userId;

        when(restTemplate.getForObject(url, Object.class)).thenReturn(mockUser);

        Object user = mockApiService.getUsersById(userId);

        assertNotNull(user);
        assertEquals("user123", user, "The returned user should be 'user123'");
    }
}
