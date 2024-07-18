package com.marinamooringmanagement.repositories.metadata;

import com.marinamooringmanagement.model.entity.metadata.MooringDueServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MooringDueServiceStatusRepository extends JpaRepository<MooringDueServiceStatus, Integer> {

    Optional<MooringDueServiceStatus> findByStatus(String status);

}