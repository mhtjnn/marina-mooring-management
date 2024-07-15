package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and manipulating state data in the database.
 * This interface extends JpaRepository and provides methods for common CRUD operations on State entities.
 */
@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    /**
     * Finds a state by its name.
     *
     * @param name The name of the state to search for.
     * @return An Optional containing the state if found, or an empty Optional otherwise.
     */
    Optional<State> findByName(String name);
}

