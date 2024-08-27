package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    Optional<Image> findByImageData(byte[] imageByteArray);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Image( " +
            "i.id, i.imageName, i.imageData, i.note) " +
            "FROM Image i " +
            "LEFT JOIN i.mooring m " +
            "WHERE (:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "ORDER BY i.id ")
    List<Image> findImagesByMooringId(@Param("mooringId") Integer mooringId);
}
