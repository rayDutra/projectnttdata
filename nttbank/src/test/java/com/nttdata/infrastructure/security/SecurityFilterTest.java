package com.nttdata.infrastructure.security;

import com.nttdata.domain.entity.User;
import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        ReflectionTestUtils.setField(securityFilter, "tokenService", tokenService);
        ReflectionTestUtils.setField(securityFilter, "repository", userRepository);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        String token = "valid-token";
        String subject = "testUser";
        User user = new User();
        user.setLogin(subject);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenService.getSubject(token)).thenReturn(subject);
        when(userRepository.findByLogin(subject)).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assert(authentication != null);
        assert(authentication.getPrincipal() instanceof User);
        assert(authentication.getName().equals(subject));

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "invalid-token";
        String subject = "invalidUser";
        String method = "GET";
        String uri = "/algum-caminho";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(uri);
        when(tokenService.getSubject(token)).thenReturn(subject);
        when(userRepository.findByLogin(subject)).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(0)).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(printWriter, times(1)).write("{\"error\": \"Token nao informado\"}");  // Espera que a resposta de erro tenha sido escrita
    }


}

