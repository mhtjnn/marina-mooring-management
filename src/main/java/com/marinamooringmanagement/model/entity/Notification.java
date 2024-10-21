package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name = "send_to_id")
    private Integer sendToId;

    @Column(name = "notification_message")
    private String notificationMessage;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "entity_str")
    private String entityStr;

    @Column(name = "entity_id")
    private Integer entityId;

    Notification(Integer id, String notificationMessage, boolean isRead) {
        this.id = id;
        this.notificationMessage = notificationMessage;
        this.isRead = isRead;
    }
}
