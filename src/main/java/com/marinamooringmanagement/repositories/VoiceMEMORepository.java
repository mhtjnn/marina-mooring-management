package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.VoiceMEMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoiceMEMORepository extends JpaRepository<VoiceMEMO, Integer> {
}
