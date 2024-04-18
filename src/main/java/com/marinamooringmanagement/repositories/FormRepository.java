package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * The FormRepository interface provides data access methods for the {@link Form} entity.
 *
 * This repository extends {@link JpaRepository}, providing standard CRUD operations and additional methods for querying forms
 * based on specific attributes such as form name.
 */
@Repository
public interface FormRepository extends JpaRepository<Form, Integer> {

    /**
     * Finds a form by its name.
     *
     * @param formName The name of the form to be searched.
     * @return An {@link Optional} containing the found {@link Form} if it exists, or an empty {@link Optional} otherwise.
     */
    Optional<Form> findByFormName(String formName);
}
