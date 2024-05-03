package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchUsersResponse implements Serializable {

    /**
     * The message associated with the response.
     */
    private String message;

    /**
     * The HTTP status code of the response.
     */
    private Integer status;

    /**
     * List of errors or error messages.
     */
    private List<String> errorList;

    /**
     * The timestamp when the response was generated.
     */
    private Timestamp time;

    /**
     * The content object of the response.
     */
    private List<Object> content;

}
