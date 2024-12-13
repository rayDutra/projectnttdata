package com.nttdata.web.controller;

import com.nttdata.domain.entity.User;
import com.nttdata.application.dto.UserDTO;
import com.nttdata.infrastructure.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticatorControllerTest {

    @Mock
    private AuthenticationManager manager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticatorController authenticatorController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticatorController).build();
    }

    @Test
    void testEfetuarLogin_Success() throws Exception {
        UserDTO userDTO = new UserDTO("testUser", "testPassword");
        User user = new User();
        user.setId(1L);
        user.setLogin("testUser");
        String mockToken = "mocked-jwt-token";

        var authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getLogin(), userDTO.getPassword());
        Authentication authentication = mock(Authentication.class);

        when(manager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.gerarToken(user)).thenReturn(mockToken);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"testUser\",\"password\":\"testPassword\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(mockToken));

        verify(manager, times(1)).authenticate(authenticationToken);
        verify(tokenService, times(1)).gerarToken(user);
    }



}
