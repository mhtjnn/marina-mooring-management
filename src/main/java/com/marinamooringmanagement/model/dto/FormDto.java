package com.marinamooringmanagement.model.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * The FormDto class represents a data transfer object for a form.
 *
 * It contains information about the form, such as the form ID, customer name, form name,
 * submission date, form type, customer ID, form file, and download URL.
 * This DTO extends {@link BaseDto}, inheriting common properties.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDto extends BaseDto {

    /** The unique identifier of the form. */
    private Integer id;

    /** The name of the customer associated with the form. */
    private String customerName;

    /** The name of the form. */
    private String formName;

    /** The date when the form was submitted. */
    private Date submittedDate;

    /** The MIME type of the form (e.g., "application/pdf" or "image/png"). */
    private String formType;

    /** The ID of the customer associated with the form. */
    private String customerId;

    /** The binary file data of the form. */
    private byte[] formFile;

    /** The URL for downloading the form. */
    private String downloadUrl;
}
