package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    Token findTokenEntityByToken(String token);
}
