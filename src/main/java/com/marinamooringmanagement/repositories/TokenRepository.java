package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Token}.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    /**
     * Find a token entity by its token value.
     *
     * @param token the token value to search for
     * @return an optional containing the token entity corresponding to the given token value, or empty if not found
     */
    Optional<Token> findTokenEntityByToken(String token);

    /**
     * Find a token entity by its refresh token value.
     *
     * @param refreshToken the refresh token value to search for
     * @return an optional containing the token entity corresponding to the given refresh token value, or empty if not found
     */
    List<Token> findTokenEntityByRefreshToken(String refreshToken);

    /**
     * Find a list of Token entities of a user using the ID of the user.
     *
     * @param userId the ID of the User
     * @return a list of Tokens associated with the given user ID
     */
    List<Token> findByUserId(Integer userId);

    Token findByToken(final String token);
}

