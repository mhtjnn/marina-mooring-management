package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Country;
import com.marinamooringmanagement.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByName(String name);
}
