package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;

public interface PasswordResetService {

    void saveToken(Token token);

    void saveToken(final UserDto user, final String token);

    String createPasswordResetToken(String email);
}
