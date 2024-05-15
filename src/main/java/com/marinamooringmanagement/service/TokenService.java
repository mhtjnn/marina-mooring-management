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
     * @param refreshToken The refresh token string
     */
    void saveToken(final UserDto emp, final String token, final String refreshToken);

    /**
     * Creates a reset password token for the given email.
     * @param email The email address of the user
     * @return The reset password token
     */
    String createPasswordResetToken(final String email);
}
