package com.marinamooringmanagement.service;

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
    BasicRestResponse uploadForm(String customerName, String customerId, MultipartFile file);

    /**
     * Downloads a form from the database.
     *
     * @param fileName The name of the file to be downloaded.
     * @return A {@link ResponseEntity} containing a {@link ByteArrayResource} representing the downloaded form file.
     */
    ResponseEntity<ByteArrayResource> downloadForm(String fileName);

    /**
     * Fetches a list of forms from the database with pagination and sorting options.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize The size of each page for pagination.
     * @param sortBy The field to sort the results by.
     * @param sortDir The direction of the sort (ascending or descending).
     * @return A {@link BasicRestResponse} object containing a list of {@link com.marinamooringmanagement.model.response.FormResponseDto} objects representing the forms.
     */
    BasicRestResponse fetchForms(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
}
