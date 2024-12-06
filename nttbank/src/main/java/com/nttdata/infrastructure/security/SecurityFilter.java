package com.nttdata.infrastructure.security;

import com.nttdata.infrastructure.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (isPermitAllRoute(requestURI, request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        var tokenJWT = recuperarToken(request);

        if (tokenJWT == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token nao informado\"}");
            response.getWriter().flush();
            return;
        }

        try {
            var subject = tokenService.getSubject(tokenJWT);
            var optionalUsuario = repository.findByLogin(subject);

            if (optionalUsuario.isPresent()) {
                var usuario = optionalUsuario.get();
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token invalido ou expirado\"}");
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
    private boolean isPermitAllRoute(String uri, String method) {
        return (method.equals("POST") && uri.matches("/login|/api/users|/api/users/upload|/accounts"))
            || (method.equals("GET") && uri.matches("/api/users/(\\d+)/export|/api/users/export|/v3/api-docs.*|/swagger-ui.*"));
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
    }
