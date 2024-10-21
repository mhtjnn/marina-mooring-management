package com.marinamooringmanagement.repositories.metadata;

import com.marinamooringmanagement.model.entity.metadata.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing and manipulating country data in the database.
 * This interface extends JpaRepository and provides methods for common CRUD operations on Country entities.
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    /**
     * Finds a country by its name.
     *
     * @param name The name of the country to search for.
     * @return An Optional containing the country if found, or an empty Optional otherwise.
     */
    Optional<Country> findByName(String name);
}

