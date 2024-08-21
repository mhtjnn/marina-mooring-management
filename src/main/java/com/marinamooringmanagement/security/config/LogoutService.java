package com.marinamooringmanagement.security.config;

import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService{

    private final TokenRepository tokenRepository;

    private final JwtUtil jwtUtil;

    public void logout(
            HttpServletRequest request

    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            return;
        }

        jwt = authHeader.substring(7);
        Integer userId = jwtUtil.getUserIdFromToken(jwt);
        final Token token = tokenRepository.findByToken(jwt);
        if(null != token && !ObjectUtils.notEqual(userId, token.getUser().getId())) {
            tokenRepository.delete(token);
        }
    }
}
