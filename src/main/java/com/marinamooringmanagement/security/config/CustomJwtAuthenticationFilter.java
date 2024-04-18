package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.security.model.AuthenticationDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom Authentication Filter extending {@link OncePerRequestFilter}.
 * This filter is responsible for extracting and validating JWT tokens from incoming requests.
 */
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenUtil;

    /**
     * Filter function that processes incoming requests to extract and validate JWT tokens.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @param chain    the filter chain
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        try {
            final String jwtToken = extractJwtFromRequest(request);
            if (StringUtils.hasText(jwtToken) && jwtTokenUtil.validateToken(jwtToken)) {
                final UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(jwtToken),
                        org.apache.commons.lang3.StringUtils.EMPTY, jwtTokenUtil.getRolesFromToken(jwtToken));
                final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                AuthenticationDetails authDetails = new AuthenticationDetails();
                authDetails.setLoggedInUserEmail(jwtTokenUtil.getUsernameFromToken(jwtToken));
                authDetails.setLoggedInUserId(jwtTokenUtil.getUserIdFromToken(jwtToken));
                authDetails.setLoggedInUserRole(jwtTokenUtil.getRoleFromToken(jwtToken));
                usernamePasswordAuthenticationToken.setDetails(authDetails);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException | BadCredentialsException ex) {
            request.setAttribute("exception", ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
        chain.doFilter(request, response);
    }

    /**
     * Function to extract JWT token from the Request.
     *
     * @param request the HTTP servlet request
     * @return the extracted JWT token as a {@link String}
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
