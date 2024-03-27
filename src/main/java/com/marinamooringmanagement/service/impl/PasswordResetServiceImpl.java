package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.DBOperationException;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.service.PasswordResetService;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

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

    @Override
    public void saveToken(UserDto userDto, String token) {
        try {
            User user = User.builder().build();
            user.setId(userDto.getId());
            final Date expireDateTime = jwtUtil.getExpireTimeFromToken(token);
            final Token passwordResetToken = Token.builder()
                    .token(token)
                    .user(user)
                    .expireAt(expireDateTime)
                    .build();
            passwordResetToken.setCreationDate(new Date(System.currentTimeMillis()));
            passwordResetToken.setLastModifiedDate(new Date(System.currentTimeMillis()));

            saveToken(passwordResetToken);
        } catch (Exception e) {
            throw new DBOperationException(e.getMessage(), e);
        }
    }

    @Override
    public String createPasswordResetToken(String email) {
        try {
            if(userRepository.findByEmail(email).isEmpty()) {
                throw new ClassNotFoundException("User with given email not found!!!");
            }
            User user = userRepository.findByEmail(email).get();
            UserDto userDto = UserDto.builder().build();
            userMapper.mapToUserDto(userDto, user);
            String resetPasswordToken =  jwtUtil.generateResetPasswordToken(email);
            saveToken(userDto, resetPasswordToken);
            return resetPasswordToken;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
