package com.marinamooringmanagement.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.security.util.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Custom Authentication Filter extending {@link OncePerRequestFilter}.
 * This filter is responsible for extracting and validating JWT tokens from incoming requests.
 */
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtTokenUtil;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

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
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain chain
    ) throws ServletException, IOException {
        try {
            final String jwtToken = extractJwtFromRequest(request);
            if (StringUtils.isNotEmpty(jwtToken) && jwtTokenUtil.validateToken(jwtToken)) {
                final UserDetails userDetails = new User(jwtTokenUtil.getUsernameFromToken(jwtToken),
                        StringUtils.EMPTY, jwtTokenUtil.getRolesFromToken(jwtToken));
                final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                final AuthenticationDetails authDetails = new AuthenticationDetails();
                authDetails.setLoggedInUserEmail(jwtTokenUtil.getUsernameFromToken(jwtToken));
                authDetails.setLoggedInUserId(jwtTokenUtil.getUserIdFromToken(jwtToken));
                authDetails.setLoggedInUserRole(jwtTokenUtil.getRoleFromToken(jwtToken));
                usernamePasswordAuthenticationToken.setDetails(authDetails);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException | BadCredentialsException ex) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); // Set the HTTP status code

            BasicRestResponse restResponse = new BasicRestResponse();
            restResponse.setMessage(ex.getLocalizedMessage());
            restResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            restResponse.setTime(new Timestamp(System.currentTimeMillis()));

            String jsonResponse = objectMapper().writeValueAsString(restResponse); // Convert the response object to JSON

            response.getWriter().write(jsonResponse); // Write the JSON response to the response body
            response.getWriter().flush();

            return; // Terminate the method execution
        }
        chain.doFilter(request, response);
    }

    /**
     * Function to extract JWT token from the Request.
     *
     * @param request the HTTP servlet request
     * @return the extracted JWT token as a {@link String}
     */
    private String extractJwtFromRequest(final HttpServletRequest request) {
        final String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
