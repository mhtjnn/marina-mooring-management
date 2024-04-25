package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    /**
     * Retrieves a page of forms from the database based on the provided specification and pagination criteria.
     *
     * <p>This method executes a query based on the provided {@code specification} to filter forms and returns a page of results according to the pagination criteria specified by the {@code pageable} parameter.
     *
     * @param specification The specification used to filter forms.
     * @param pageable      The pagination criteria specifying the page number, page size, and sorting order.
     * @return A {@code Page} containing the forms that match the given specification, according to the specified pagination criteria.
     * @see Specification
     * @see Pageable
     * @see Page
     */
    Page<Form> findAll(Specification<Form> specification, Pageable pageable);
}
