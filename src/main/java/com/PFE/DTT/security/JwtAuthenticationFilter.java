package com.PFE.DTT.security;

import com.PFE.DTT.model.User;
import com.PFE.DTT.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip filter for public endpoints, auth endpoints, and protocols
        return path.startsWith("/api/public/") || 
               path.startsWith("/api/auth/") ||
               path.startsWith("/api/protocols");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");
        
        logger.info("Processing request: {}", requestURI);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header or not a Bearer token for request: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        String email = null;

        try {
            // Extract email from token
            email = jwtUtil.extractEmail(jwt);
            logger.info("Extracted email from token: {}", email);
            
            if (email == null) {
                logger.warn("Could not extract email from token");
                filterChain.doFilter(request, response);
                return;
            }

            // Only process if no authentication is set yet
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Get user from database
                User user = userRepository.findByEmail(email).orElse(null);

                if (user == null) {
                    logger.warn("User not found for email: {}", email);
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Check if user is verified
                if (!user.isVerified()) {
                    logger.warn("User not verified: {}", email);
                    filterChain.doFilter(request, response);
                    return;
                }

                // Validate token
                if (!jwtUtil.isTokenExpired(jwt)) {
                    // Extract role from token
                    String role = jwtUtil.extractRole(jwt);
                    
                    logger.info("User role from token: {}", role);
                    
                    if (role == null) {
                        logger.warn("Role is null in token");
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // Create authorities based on role
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, authorities
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Authentication set for user: {}", email);
                } else {
                    logger.warn("Token is expired for user: {}", email);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}