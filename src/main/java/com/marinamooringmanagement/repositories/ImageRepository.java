package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.Image( " +
            "i.id, i.imageName, i.imageData, i.note) " +
            "FROM Image i " +
            "LEFT JOIN i.mooring m " +
            "WHERE (:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "ORDER BY i.id ")
    List<Image> findImagesByMooringId(@Param("mooringId") Integer mooringId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Image (" +
            "i.id, i.imageName, i.note) " +
            "FROM Image i " +
            "LEFT JOIN i.workOrder w " +
            "WHERE w.id = :workOrderId " +
            "ORDER BY i.id")
    List<Image> findImagesByWorkOrderIdWithoutData(
            @Param("workOrderId") Integer id
    );

    List<Image> findByWorkOrderId(Integer id);
}
