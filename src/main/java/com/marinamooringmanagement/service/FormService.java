package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.FormSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * The FormService interface provides methods for handling form operations, such as uploading, downloading, and fetching forms.
 *
 * It outlines the contracts for form-related operations and the expected behavior of implementations.
 */
public interface FormService {

    /**
     * Uploads a form to the database.
     *
     * @param customerName The name of the customer who submitted the form.
     * @param customerId The ID of the customer who submitted the form.
     * @param file The form file to be uploaded.
     * @return A {@link BasicRestResponse} object containing the status of the operation and any associated messages.
     */
    BasicRestResponse uploadForm(final String customerName, final String customerId, final MultipartFile file);

    /**
     * Downloads a form from the database.
     *
     * @param fileName The name of the file to be downloaded.
     * @return A {@link ResponseEntity} containing a {@link ByteArrayResource} representing the downloaded form file.
     */
    ResponseEntity<ByteArrayResource> downloadForm(final String fileName);

    /**
     * Fetches forms based on the provided search criteria.
     *
     * @param formSearchRequest An instance of {@code FormSearchRequest} containing the search criteria.
     * @return A {@code BasicRestResponse} object containing the response data, including the list of forms matching the search criteria.
     * @throws IllegalArgumentException if {@code formSearchRequest} is {@code null}.
     * @implNote The implementation of this method should handle various search criteria specified in the {@code formSearchRequest} and return the appropriate response.
     * @apiNote The returned {@code BasicRestResponse} includes a list of forms matching the search criteria.
     * @see FormSearchRequest
     * @see BasicRestResponse
     */
    BasicRestResponse fetchForms(final FormSearchRequest formSearchRequest);
}
