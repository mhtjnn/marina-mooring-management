package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.ServiceArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Integer> {
}
