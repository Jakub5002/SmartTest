package com.example.backend.security;

import com.example.backend.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            if(jwtUtils.validateToken(token)){
                email = jwtUtils.getEmailFromToken(token);
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            List<String> roles = jwtUtils.getRolesFromToken(token);
            List<SimpleGrantedAuthority> authorities = roles.stream().map(role ->new SimpleGrantedAuthority(role)).toList();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request,response);
    }
}
