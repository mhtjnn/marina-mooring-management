package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.repositories.QBO.QBOUserRepository;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogoutService{

    private final TokenRepository tokenRepository;

    private final JwtUtil jwtUtil;

    @Autowired
    private QBOUserRepository qboUserRepository;

    @Transactional
    public void logout(
            HttpServletRequest request

    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return;
        }

        jwt = authHeader.substring(7);
        final Token token = tokenRepository.findByToken(jwt);
        if(null != token) {
            tokenRepository.delete(token);
            if(null != token.getUser()) {
                if(null != token.getUser().getEmail()) {
                    Optional<QBOUser> optionalQBOUser = qboUserRepository.findQBOUserByCreatedBy(token.getUser().getEmail());
                    optionalQBOUser.ifPresent(qboUser -> qboUserRepository.delete(qboUser));
                }
            }
        }
    }
}
