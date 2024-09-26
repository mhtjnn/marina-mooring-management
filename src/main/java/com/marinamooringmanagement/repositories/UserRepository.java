package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.User;
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
    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName, u.email, u.password, " +
            "r.id, r.name) " +
            "FROM User u " +
            "LEFT JOIN u.role r " +
            "WHERE (:email IS NOT NULL AND u.email = :email)"
    )
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName, u.email, u.password, " +
            "r.id, r.name, i.id, i.imageData) " +
            "FROM User u " +
            "LEFT JOIN u.role r " +
            "LEFT JOIN u.image i " +
            "WHERE (:email IS NOT NULL AND u.email = :email)"
    )
    Optional<User> findByEmailWithImage(@Param("email") String email);

    /**
     * Find all user entities matching the given specification.
     *
     * @return a list of user entities matching the given specification
     */
    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName, u.email, u.phoneNumber, " +
            "u.address, u.zipCode, u.customerOwnerId, u.companyName, " +
            "s.id, s.name, c.id, c.name, r.id, r.name) " +
            "FROM User u " +
            "LEFT JOIN u.role r " +
            "LEFT JOIN u.state s " +
            "LEFT JOIN u.country c " +
            "WHERE ((:customerOwnerId = -1 AND r.id = 2) " +
            "OR ((r.id = 3 OR r.id = 4) AND u.customerOwnerId = :customerOwnerId)) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(u.firstName) LIKE CONCAT('%', LOWER(:searchText), '%') " +
            "OR LOWER(u.lastName) LIKE CONCAT('%', LOWER(:searchText), '%') " +
            "OR (LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "OR LOWER(u.email) LIKE CONCAT('%', LOWER(:searchText), '%') " +
            "OR LOWER(u.phoneNumber) LIKE CONCAT('%', LOWER(:searchText), '%') " +
            "OR LOWER(r.name) LIKE CONCAT('%', LOWER(:searchText), '%'))) " +
            "ORDER BY u.companyName")
    List<User> findAll(@Param("customerOwnerId") Integer customerOwnerId,
                       @Param("searchText") String searchText);

    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, i.id, i.imageData) " +
            "FROM User u " +
            "LEFT JOIN u.image i " +
            "LEFT JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND u.id IS NOT NULL AND u.id = :userId)"
    )
    Optional<User> findByIdWithImage(@Param("userId") Integer userId);

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
            "WHERE r.id = :roleId " +
            "AND (:customerOwnerId IS NOT NULL AND u.customerOwnerId IS NOT NULL AND u.customerOwnerId = :customerOwnerId) " +
            "AND (:searchText IS NOT NULL AND LOWER(CAST(u.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(r.name) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
            "ORDER BY u.id")
    List<User> findAllUsersByCustomerOwnerAndRoleMetadata(@Param("roleId") Integer roleId,
                                                          @Param("customerOwnerId") Integer customerOwnerId,
                                                          @Param("searchText") String searchText);

    @Query("SELECT new com.marinamooringmanagement.model.entity.User( " +
            "u.id, u.firstName, u.lastName, u.email, u.phoneNumber, " +
            "u.customerOwnerId, u.companyName, u.address, u.zipCode," +
            "r.id, r.name, " +
            "s.id, s.name, " +
            "c.id, c.name) " +
            "FROM User u " +
            "LEFT JOIN u.state s " +
            "LEFT JOIN u.country c " +
            "LEFT JOIN u.role r " +
            "WHERE u.id = :id")
    Optional<User> findUserByIdWithoutImage(@Param("id") Integer customerOwnerId);
}
