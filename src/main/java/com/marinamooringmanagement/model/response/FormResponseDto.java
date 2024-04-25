package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * The FormResponseDto class represents a data transfer object for a form response.
 *
 * This DTO is used to transfer data about a form response, including form ID, the name of the person who submitted it,
 * form name, submission date, and the URL for downloading the form.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormResponseDto {

    /**
     * The unique identifier of the form.
     */
    private Integer id;

    /**
     * The name of the person who submitted the form.
     */
    private String customerName;

    /**
     * The name of the form.
     */
    private String formName;

    /**
     * The date when the form was submitted.
     */
    private Date submittedDate;

    /**
     * The URL for downloading the form.
     */
    private String downloadUrl;
}
