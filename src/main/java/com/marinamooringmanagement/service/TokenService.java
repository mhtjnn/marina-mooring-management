package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;

/**
 * Interface for Token Service.
 * This interface defines methods related to token management.
 */
public interface TokenService {

    /**
     * Saves a token in the database.
     * @param token The token to save
     */
    void saveToken(final Token token);

    /**
     * Saves a token associated with a user.
     * @param emp The user DTO
     * @param token The token string
     */
    void saveToken(final UserDto emp, final String token);

    /**
     * Create a Reset Password Token
     * @param email Email given by User
     * @return
     */
    String createPasswordResetToken(String email);
}
