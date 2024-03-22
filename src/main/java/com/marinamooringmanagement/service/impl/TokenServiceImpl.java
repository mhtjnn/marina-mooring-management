package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Implementation class for Token Service Interface.
 * This class provides implementation for saving tokens in the database.
 */
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Saves the given token entity to the database.
     * @param token The token entity to save
     * @throws DBOperationException if an error occurs during database operation
     */
    @Override
    public void saveToken(Token token) {
        try {
            if(null != token) {
                tokenRepository.save(token);
            }
        } catch (Exception e) {
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    /**
     * Saves a token with user details to the database.
     * @param userDto The user details DTO
     * @param token The authentication token
     * @throws DBOperationException if an error occurs during database operation
     */
    @Override
    public void saveToken(final UserDto userDto, final String token) {
        try {
            if(null != userDto && StringUtils.hasText(token)) {
                final User user = User.builder().build();
                user.setId(userDto.getId());
                final Date expireDateTime = jwtUtil.getExpireTimeFromToken(token);
                final Token tokenEntity = Token.builder()
                        .user(user)
                        .token(token)
                        .expireAt(expireDateTime)
                        .build();
                saveToken(tokenEntity);
            }
        } catch (Exception e) {
            log.error("exception occurred while saving token " + e.getMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }
}
