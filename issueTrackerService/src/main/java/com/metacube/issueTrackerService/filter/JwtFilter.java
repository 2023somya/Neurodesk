package com.metacube.issueTrackerService.filter;



import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.metacube.issueTrackerService.utilis.JwtUtil;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter  extends OncePerRequestFilter{
//    @Autowired
//    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        	
        	String token = authorizationHeader.substring(7).trim();// remove "Bearer " and any whitespace
        	token = token.trim().replaceAll("\\s", ""); // removes all whitespace (not just leading/trailing)

            String createdBy = jwtUtil.extractUsername(token);
            
            if (createdBy == null || createdBy.isEmpty()) {
                throw new RuntimeException("createdBy is null or empty after parsing JWT.");
            }
            
            
        	 // Validate format
            if (token.isEmpty() || token.contains(" ")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token format");
                return;
            }
        	
        	
        	if (!authorizationHeader.startsWith("Bearer ") || authorizationHeader.trim().split("\\s+").length != 2) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Authorization header format. Expected 'Bearer <token>'");
                return; // stop further processing
            }
        	
            jwt = authorizationHeader.substring(7);
            try {
            	System.out.println("Raw Authorization Header: >" + authorizationHeader + "<");
            	System.out.println("Extracted token: >" + token + "<");
                username = jwtUtil.extractUsername(jwt);  // Extract email
                String role = jwtUtil.extractUserRole(token); // Extract role
                System.out.println("Setting authentication for user: " + username + " with role: ROLE_" + role);


                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("Setting authentication for user: " + username + " with role: ROLE_" + role);

                }

            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
            
        }
        else {
            throw new IllegalArgumentException("Authorization header is missing or malformed");
        }

        chain.doFilter(request, response);
    }
}