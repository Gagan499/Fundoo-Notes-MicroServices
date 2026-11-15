package com.example.AuthService.config;

import com.example.AuthService.service.UserDetailsServiceImpl;
import com.example.AuthService.utils.Jwtutils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Jwtutils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(Jwtutils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(jwtUtils.extractEmail(token));
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (UsernameNotFoundException exception) {
                    SecurityContextHolder.clearContext();
                    log.debug("JWT references a user account that no longer exists");
                }
            } else {
                log.debug("Rejected invalid bearer token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
