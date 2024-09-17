package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Form;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Integer>, JpaSpecificationExecutor<Form> {
    @Query("SELECT new com.marinamooringmanagement.model.entity.Form(" +
            "f.id, f.formName, f.fileName, f.createdBy, f.creationDate, " +
            "u.id, u.firstName, u.lastName, r.id, r.name) " +
            "FROM Form f " +
            "JOIN f.user u " +
            "JOIN u.role r " +
            "WHERE (:searchText IS NOT NULL AND f.parentFormId IS NULL AND " +
            "(f.formName IS NOT NULL AND LOWER(f.formName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(f.fileName IS NOT NULL AND LOWER(f.fileName) LIKE LOWER(CONCAT('%', :searchText, '%')))  OR " +
            "(f.createdBy IS NOT NULL AND LOWER(f.createdBy) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY f.id")
    List<Form> findAllWithoutFormData(@Param("searchText") String searchText,
                                      @Param("userId") Integer userId);

    List<Form> findAll(Specification<Form> spec);


    @Query("SELECT new com.marinamooringmanagement.model.entity.Form(" +
            "f.id, f.formName, f.fileName) " +
            "FROM Form f " +
            "LEFT JOIN f.workOrder w " +
            "WHERE w.id = :workOrderId " +
            "ORDER BY f.id")
    List<Form> findFormsByWorkOrderIdWithoutData(
            @Param("workOrderId") Integer id
    );

    @Query("SELECT new com.marinamooringmanagement.model.entity.Form(" +
            "f.id, f.formName, f.fileName, f.createdBy, f.creationDate, " +
            "u.id, u.firstName, u.lastName, r.id, r.name) " +
            "FROM Form f " +
            "JOIN f.user u " +
            "JOIN u.role r " +
            "WHERE f.id = :id")
    Form findByIdWithoutData(@Param(("id")) Integer id);
}