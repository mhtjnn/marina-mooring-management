package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.FormResponseDto;
import com.marinamooringmanagement.repositories.FormRepository;
import com.marinamooringmanagement.service.FormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The FormServiceImpl class provides the implementation for handling form operations, including uploading, downloading, and fetching forms.
 *
 * It manages interactions with the FormRepository for data access and conversion between Form entities and data transfer objects.
 */
@Service
public class FormServiceImpl implements FormService {

    private static final Logger log = LoggerFactory.getLogger(FormServiceImpl.class);

    @Autowired
    private FormRepository formRepository;

    /**
     * Uploads a form to the database.
     *
     * @param customerName The name of the customer who submitted the form.
     * @param customerId The ID of the customer who submitted the form.
     * @param file The form file to be uploaded.
     * @return A {@link BasicRestResponse} object containing the status of the operation and any associated messages.
     */
    @Override
    public BasicRestResponse uploadForm(final String customerName, final String customerId, final MultipartFile file) {
        log.info("Upload form API called");
        if(file.isEmpty()) throw new RuntimeException("No file given for upload");
        if(null == file.getOriginalFilename()) throw new RuntimeException("No file name for given given");
        final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/form/download/")
                    .path(fileName)
                    .toUriString();
            final Form form = Form.builder()
                    .customerName(customerName)
                    .customerId(customerId)
                    .formName(fileName)
                    .submittedDate(new Date(System.currentTimeMillis()))
                    .formFile(file.getBytes())
                    .formType(file.getContentType())
                    .downloadUrl(downloadUrl)
                    .build();
            form.setCreationDate(new Date(System.currentTimeMillis()));
            form.setLastModifiedDate(new Date(System.currentTimeMillis()));
            formRepository.save(form);
            response.setMessage("Form saved successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while uploading form in the database", e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Downloads a form from the database.
     *
     * @param fileName The name of the file to be downloaded.
     * @return A {@link ResponseEntity} containing a {@link ByteArrayResource} representing the downloaded form file.
     */
    @Override
    public ResponseEntity<ByteArrayResource> downloadForm(final String fileName) {
        try {
            log.info("Download form API called");
            final Optional<Form> optionalForm = formRepository.findByFormName(fileName);
            if (optionalForm.isEmpty()) {
                throw new ResourceNotFoundException("No file found with the given filename: " + fileName);
            }
            final Form form = optionalForm.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(form.getFormType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "file; filename=\"" + form.getFormName() + "\"")
                    .body(new ByteArrayResource(form.getFormFile()));
        } catch (Exception e) {
            log.error("Error occurred while downloading form", e);
            byte[] emptyByteArray = new byte[0];
            return ResponseEntity.internalServerError()
                    .body(new ByteArrayResource(emptyByteArray));
        }
    }

    /**
     * Fetches a list of forms from the database with pagination and sorting options.
     *
     * @param pageNumber The page number for pagination.
     * @param pageSize The size of each page for pagination.
     * @param sortBy The field to sort the results by.
     * @param sortDir The direction of the sort (ascending or descending).
     * @return A {@link BasicRestResponse} object containing a list of {@link FormResponseDto} objects representing the forms.
     */
    @Override
    public BasicRestResponse fetchForms(final Integer pageNumber, final Integer pageSize, final String sortBy, final String sortDir) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            final Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            final Page<Form> formList = formRepository.findAll(pageable);
            log.info("Fetching all forms from the database");
            final List<FormResponseDto> formResponseDtoList = formList.stream().map(this::mapToFormResponseDto).collect(Collectors.toList());
            response.setMessage("Form list fetched successfully");
            response.setContent(formResponseDtoList);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            log.error("Error occurred while fetching all forms from the database", e);
            response.setMessage(e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Maps a {@link Form} entity to a {@link FormResponseDto}.
     *
     * @param form The form entity to be mapped.
     * @return A {@link FormResponseDto} object containing the form data.
     */
    private FormResponseDto mapToFormResponseDto(final Form form) {
        return FormResponseDto.builder()
                .id(form.getId())
                .submittedBy(form.getCustomerName())
                .submittedDate(form.getSubmittedDate())
                .downloadUrl(form.getDownloadUrl())
                .formName(form.getFormName())
                .build();
    }
}