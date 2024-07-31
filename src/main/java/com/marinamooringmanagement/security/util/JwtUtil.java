package com.marinamooringmanagement.security.util;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.*;
/**
 * Utility class for JWT token handling.
 */
@Service
public class JwtUtil extends GlobalExceptionHandler {

    @Autowired
    TokenRepository tokenRepository;

    @Value("${security.token.expiration.time}")
    private Long normalTokenExpirationTime;

    @Value("${security.resetToken.expiration.time}")
    private Long resetPasswordTokenExpirationTime;

    @Value("${security.refreshToken.expiration.time}")
    private Long refreshTokenExpirationTime;

    @Value("${security.secret.key}")
    private String SECRET_KEY;

    /**
     * Function to validate a JWT token.
     *
     * @param authToken the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(final String authToken) {
        return validate(authToken, SECRET_KEY) && isTokenValid(authToken);
    }

    /**
     * Function to check if a token is valid based on database and expiration.
     *
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    private boolean isTokenValid(final String token) {
        final Optional<Token> optionalToken = tokenRepository.findTokenEntityByToken(token);
        if(optionalToken.isPresent()) {
            final Token tokenEntity = optionalToken.get();

            if (new Date().before(tokenEntity.getTokenExpireAt())) {
                final User user = tokenEntity.getUser();
                return user != null;
            }
        } else {
            throw new RuntimeException("Token NOT FOUND!!!");
        }

        return false;
    }

    /**
     * Function to validate a token signature.
     *
     * @param authToken the JWT token
     * @param secretKey the secret key used for signing
     * @return true if the signature is valid, false otherwise
     */
    private boolean validate(final String authToken, final String secretKey) {
        try {
            Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | ExpiredJwtException ex) {
            throw ex;
        }
    }

    /**
     * Function to get Username from the JWT
     * @param token the JWT Token
     * @return username extracted from the token
     */
    public String getUsernameFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * Extract roles from the JWT
     * @param token The JWT Token
     * @return Roles given to the User extracted from the Token
     */
    public List<SimpleGrantedAuthority> getRolesFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
        final List<SimpleGrantedAuthority> roles = new ArrayList<>();
        final List<String> rolesFromToken = claims.get("roles", List.class);
        if(null != rolesFromToken) {
            rolesFromToken.forEach(roleFromToken -> roles.add(new SimpleGrantedAuthority(roleFromToken)));
        }
        return roles;
    }

    /**
     * Function to get User Id from the JWT
     * @param token The JWT Token
     * @return User ID of the User extracted from the token
     */
    public Integer getUserIdFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
        return claims.get("id", Integer.class);
    }

    /**
     * Function to extract roles from the JWT
     * @param token The JWT Token
     * @return Get the first Role from the List of Roles given to the User
     */
    public String getRoleFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
        final List<String> rolesFromToken = claims.get("roles", List.class);
        if(!CollectionUtils.isEmpty(rolesFromToken)) {
            return rolesFromToken.get(0);
        }
        return null;
    }

    /**
     * Function to generate a JWT token based on user details.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(final UserDto userDetails, final String tokenType) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("roles", Arrays.asList(userDetails.getRole().getName()));
        claims.put("id", userDetails.getId());

        return doGenerateToken(claims, userDetails.getEmail(), tokenType);
    }

    /**
     * Helper function for generating JWT
     * @param claims Roles and ID
     * @param subject Email
     * @return The JWT Token
     */
    public String doGenerateToken(Map<String, Object> claims, final String subject, final String tokenType) {

        Long diffTokenExpirationTime = StringUtils.equals(tokenType, "NORMAL_TOKEN") ? normalTokenExpirationTime :
                StringUtils.equals(tokenType, "REFRESH_TOKEN") ? refreshTokenExpirationTime :
                        resetPasswordTokenExpirationTime;

        return Jwts.builder()
                .setClaims(claims != null ? claims : new HashMap<>())
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + diffTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, getSignInKey())
                .compact();
    }

    /**
     * Function to get Expiration time of the JWT
     * @param token
     * @return {@link Date}
     */
    public Date getExpireTimeFromToken(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

    /**
     * Function to get {@link Key} using the SECRET_KEY.
     *
     * @return the generated {@link Key}
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}