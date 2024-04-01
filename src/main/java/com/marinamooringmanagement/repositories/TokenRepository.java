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
     * @return the token entity corresponding to the given token value, or null if not found
     */
    Optional<Token> findTokenEntityByToken(String token);

    /**
     * Find a list of Token entities of a user using ID of the user.
     * @param userId Id of the User
     * @return List of Tokens
     */
    List<Token> findByUserId(Integer userId);
}
