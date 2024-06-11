package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.security.util.JwtUtil;
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

    private static final String resetPasswordTokenStr = "RESET_PASSWORD_TOKEN";

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

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
    public void saveToken(final Token token) {
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
     * @param normalToken The authentication token
     * @throws DBOperationException if an error occurs during database operation
     */
    @Override
    public void saveToken(final UserDto userDto, final String normalToken, final String refreshToken) {
        try {
            if(null != userDto && StringUtils.hasText(normalToken)) {
                final User user = User.builder().build();
                user.setId(userDto.getId());
                final Date tokenExpireDateTime = jwtUtil.getExpireTimeFromToken(normalToken);
                Date refreshTokenExpireDateTime = null;
                if(null != refreshToken) refreshTokenExpireDateTime = jwtUtil.getExpireTimeFromToken(refreshToken);
                final Token tokenEntity = Token.builder()
                        .user(user)
                        .token(normalToken)
                        .refreshToken(refreshToken)
                        .tokenExpireAt(tokenExpireDateTime)
                        .refreshTokenExpireAt(refreshTokenExpireDateTime)
                        .build();
                tokenEntity.setCreationDate(new Date(System.currentTimeMillis()));
                tokenEntity.setLastModifiedDate(new Date(System.currentTimeMillis()));
                saveToken(tokenEntity);
            }
        } catch (Exception e) {
            log.error("exception occurred while saving token " + e.getMessage());
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    /**
     * Function to create a reset password token(which only has subject as email).
     * @param email Email given by the user
     * @return
     */
    @Override
    public String createPasswordResetToken(final String email) {
        try {
            if(userRepository.findByEmail(email).isEmpty()) {
                throw new ClassNotFoundException("User with given email not found!!!");
            }
            final User user = userRepository.findByEmail(email).get();
            final UserDto userDto = UserDto.builder().build();
            userMapper.mapToUserDto(userDto, user);
            final String resetPasswordToken =  jwtUtil.doGenerateToken(null, email, resetPasswordTokenStr);
            saveToken(userDto, resetPasswordToken, null);
            return resetPasswordToken;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
