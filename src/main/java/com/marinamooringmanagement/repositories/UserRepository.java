package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Find a user entity by email.
     *
     * @param email the email address of the user to search for
     * @return an optional containing the user entity corresponding to the given email address, or empty if not found
     */
    Optional<User> findByEmail(String email);
}


