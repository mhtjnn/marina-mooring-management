package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {

    private Integer id;

    private Integer createdById;

    private Integer sendToId;

    private String notificationMessage;

    private boolean isRead;

    private String entityStr;

    private Integer entityId;

}
