package com.nttdata.web.controller;

import com.nttdata.application.service.MockApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MockApiControllerTest {

    @Mock
    private MockApiService mockApiService;

    @InjectMocks
    private MockApiController mockApiController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mockApiController).build();
    }

    @Test
    void testGetUsers_Success() throws Exception {
        List<Object> mockUsers = List.of("User1", "User2", "User3");
        when(mockApiService.getUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/mock")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("User1"))
            .andExpect(jsonPath("$[1]").value("User2"))
            .andExpect(jsonPath("$[2]").value("User3"));

        verify(mockApiService, times(1)).getUsers();
    }

    @Test
    void testGetUsersById_Success() throws Exception {
        String mockUser = "User1";
        String userId = "1";
        when(mockApiService.getUsersById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/mock/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("User1"));

        verify(mockApiService, times(1)).getUsersById(userId);
    }


}
