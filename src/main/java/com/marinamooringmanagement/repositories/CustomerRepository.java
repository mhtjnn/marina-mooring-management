package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Customer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing Customer entities in the database.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName, c.customerId, c.address, c.city, c.notes, c.emailAddress, " +
            "s.id, s.name, co.id, co.name, c.zipCode, cu.id, cu.type, u.id, u.firstName, u.lastName, " +
            "c.phone, r.id, r.name, " +
            "qbc.id, qbc.quickbookCustomerFirstName, quickbookCustomerLastName, quickbookCustomerId) " +
            "FROM Customer c " +
            "LEFT JOIN c.state s " +
            "LEFT JOIN c.country co " +
            "LEFT JOIN c.customerType cu " +
            "LEFT JOIN c.user u " +
            "LEFT JOIN u.role r " +
            "LEFT JOIN c.quickBookCustomer qbc " +
            "WHERE (:searchText IS NOT NULL AND " +
            "(LOWER(CAST(c.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.firstName IS NOT NULL AND LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.lastName IS NOT NULL AND LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.customerId IS NOT NULL AND LOWER(c.customerId) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.emailAddress IS NOT NULL AND LOWER(c.emailAddress) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.phone IS NOT NULL AND LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(s.name IS NOT NULL AND LOWER(s.name) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(co.name IS NOT NULL AND LOWER(co.name) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY c.id")
    List<Customer> findAll(@Param("searchText") String searchText,
                           @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName) " +
            "FROM Customer c " +
            "LEFT JOIN c.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY c.id")
    List<Customer> findAllCustomerMetadata(@Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName, u.id, u.firstName, u.lastName) " +
            "FROM Customer c " +
            "LEFT JOIN c.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            " AND (:customerId IS NOT NULL AND c.id = :customerId)")
    Optional<Customer> findCustomerWithUserMetadata(@Param("userId") Integer userId,
                                                   @Param("customerId") Integer customerId);

    @Query("SELECT c " +
            "FROM Customer c " +
            "JOIN c.user u " +
            "JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId AND c.id = :customerId) " +
            "ORDER BY c.id")
    Optional<Customer> findCustomerByIdWithImages(@Param("customerId") Integer customerId,
                                                     @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName, c.customerId, c.address, c.city, c.notes, c.emailAddress, " +
            "s.id, s.name, co.id, co.name, c.zipCode, cu.id, cu.type, u.id, u.firstName, u.lastName, " +
            "c.phone, r.id, r.name) " +
            "FROM Customer c " +
            "LEFT JOIN c.state s " +
            "LEFT JOIN c.country co " +
            "LEFT JOIN c.customerType cu " +
            "JOIN c.user u " +
            "JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId AND c.id = :customerId) " +
            "GROUP BY c.id, s.id, co.id, cu.id, u.id, r.id " +
            "ORDER BY c.id")
    Optional<Customer> findCustomerByIdWithoutImages(@Param("customerId") Integer customerId,
                                                  @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName, u.id, u.firstName, u.lastName) " +
            "FROM Customer c " +
            "LEFT JOIN c.mooringList m " +
            "LEFT JOIN c.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "AND (:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "ORDER BY c.id")
    Optional<Customer> findCustomerByMooringIdMetadata(@Param("userId") Integer userId,
                                                   @Param("mooringId") Integer mooringId);



    List<Customer> findAll(final Specification<Customer> spec);

    Optional<Customer> findByEmailAddress(String emailAddress);

    Optional<Customer> findByCustomerId(String customerId);
}