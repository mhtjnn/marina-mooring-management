package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Notification;
import com.marinamooringmanagement.model.response.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.response.NotificationResponseDto(" +
            "n.id, n.creationDate, n.createdById, n.sendToId, n.notificationMessage, n.isRead, n.entityStr, n.entityId) " +
            "FROM Notification n " +
            "WHERE n.sendToId = :sendToId ")
    Page<NotificationResponseDto> findAll(
            @Param("sendToId") final Integer sendToId,
            Pageable pageable
    );

}
