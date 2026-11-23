package com.example.hisync.security;

import com.example.hisync.model.User;
import com.example.hisync.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Reads "Authorization: Bearer <token>" on each request. If the token is valid,
 * the matching User is marked as logged in for this request.
 * Created in SecurityConfig (not a @Component, so it is not registered twice).
 * Controllers can get it with {@code @AuthenticationPrincipal User user}.
 */
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIX)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(PREFIX.length()).trim();

            // Look the user up on every request so role changes and deleted
            // accounts take effect immediately, not when the token expires.
            jwtService.extractUserId(token)
                    .flatMap(userRepo::findById)
                    .ifPresent(this::authenticate);
        }

        chain.doFilter(request, response);
    }

    private void authenticate(User user) {
        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase());
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
