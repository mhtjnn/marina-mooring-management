package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * Find a user entity by email.
     *
     * @param email the email address of the user to search for
     * @return an optional containing the user entity corresponding to the given email address, or empty if not found
     */
    Optional<User> findByEmail(final String email);

    /**
     * Find all user entities matching the given specification.
     *
     * @param spec the specification to filter users
     * @return a list of user entities matching the given specification
     */
    Page<User> findAll(final Specification<User> spec, final Pageable pageable);

    Optional<User> findByPhoneNumber(String givenPhoneNumber);

    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName) " +
            "FROM User u " +
            "LEFT JOIN u.role r " +
            "WHERE (r.id = :roleId) " +
            "ORDER BY u.id")
    List<User> findAllUsersByRoleMetadata(@Param("roleId") Integer roleId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName) " +
            "FROM User u " +
            "LEFT JOIN u.role r " +
            "WHERE (r.id = :roleId) " +
            "AND (:customerOwnerId IS NOT NULL AND u.customerOwnerId = :customerOwnerId) " +
            "ORDER BY u.id")
    List<User> findAllUsersByCustomerOwnerAndRoleMetadata(@Param("roleId") Integer roleId,
                                                          @Param("customerOwnerId") Integer customerOwnerId);
}
