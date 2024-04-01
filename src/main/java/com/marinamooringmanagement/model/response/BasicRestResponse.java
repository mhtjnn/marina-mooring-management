package com.marinamooringmanagement.model.response;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Basic REST response class representing a response from RESTful API endpoints.
 * This class includes fields such as message, status code, error list, timestamp, and content object.
 */
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Data
@Builder
public class BasicRestResponse implements Serializable {

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
    private Object content;

    /**
     * Constructs a BasicRestResponse with specific status, message, and timestamp.
     *
     * @param status  the HTTP status code of the response
     * @param message the message associated with the response
     * @param time    the timestamp when the response was generated
     */
    public BasicRestResponse(final int status, final String message, final Timestamp time) {
        super();
        this.status = status;
        this.message = message;
        this.time = time;
    }
}
