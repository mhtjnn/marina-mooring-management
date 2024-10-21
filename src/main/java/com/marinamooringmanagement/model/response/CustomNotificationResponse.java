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
public class CustomNotificationResponse {

    private Integer id;

    private Date creationDate;

    private String message;

    private boolean notificationRead;

    private Object entityObj;
}
