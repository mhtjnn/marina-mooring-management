package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.MooringStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MooringStatusRepository extends JpaRepository<MooringStatus, Integer> {
}
