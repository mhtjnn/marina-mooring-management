package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto{

    private Integer id;

    private Date creationDate;

    private Integer createdById;

    private Integer sendToId;

    private String notificationMessage;

    private boolean notificationRead;

    private String entityStr;

    private Integer entityId;

}
