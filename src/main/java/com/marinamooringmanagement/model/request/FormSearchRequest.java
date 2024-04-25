package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a search request for querying form information based on specified criteria.
 *
 * <p>This class extends {@code BaseSearchRequest} and includes additional fields to define search criteria for forms.
 *
 * @see BaseSearchRequest
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormSearchRequest extends BaseSearchRequest{

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

}
