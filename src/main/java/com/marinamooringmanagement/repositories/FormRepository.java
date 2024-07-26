package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Form;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Integer> {

    Page<Form> findAll(Specification<Form> spec, Pageable p);

    List<Form> findAll(Specification<Form> spec);

}
