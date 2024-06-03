package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    private final JwtUtil jwtUtil;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return;
        }

        jwt = authHeader.substring(7);
        Integer userId = jwtUtil.getUserIdFromToken(jwt);
        List<Token> tokenList = tokenRepository.findByUserId(userId);
        if(null != tokenList) {
            tokenRepository.deleteAll(tokenList);
        }
    }
}