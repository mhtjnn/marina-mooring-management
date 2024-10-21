package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.VoiceMEMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoiceMEMORepository extends JpaRepository<VoiceMEMO, Integer> {


    @Query("SELECT new com.marinamooringmanagement.model.entity.VoiceMEMO(" +
            "v.id, v.name) " +
            "FROM VoiceMEMO v " +
            "LEFT JOIN v.workOrder w " +
            "WHERE w.id = :workOrderId " +
            "ORDER BY v.id")
    List<VoiceMEMO> findVoiceMEMOsByWorkOrderIdWithoutData(
            @Param("workOrderId") Integer id
    );

    @Query("SELECT new com.marinamooringmanagement.model.entity.VoiceMEMO(" +
            "v.id, v.name) " +
            "FROM VoiceMEMO v " +
            "WHERE v.id = :id")
    Optional<VoiceMEMO> findByIdWithoutData(@Param("id") Integer id);

    List<VoiceMEMO> findByWorkOrderId(Integer id);
}
