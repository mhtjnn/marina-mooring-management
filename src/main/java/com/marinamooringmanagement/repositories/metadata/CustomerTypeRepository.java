package com.marinamooringmanagement.repositories.metadata;

import com.marinamooringmanagement.model.entity.metadata.CustomerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerTypeRepository extends JpaRepository<CustomerType, Integer> {
    CustomerType findByType(String type);
}
