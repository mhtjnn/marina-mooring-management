package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.FormMapper;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.request.FormSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.FormResponseDto;
import com.marinamooringmanagement.repositories.FormRepository;
import com.marinamooringmanagement.service.FormService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
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

    @Autowired
    private FormMapper formMapper;

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
            response.setMessage("Form saved successfully");
            response.setContent(formMapper.mapToFormResponseDto(FormResponseDto.builder().build(), formRepository.save(form)));
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
     * Fetches forms based on the provided search criteria.
     *
     * @param formSearchRequest An instance of {@code FormSearchRequest} containing the search criteria.
     * @return A {@code BasicRestResponse} object containing the response data, including the list of forms matching the search criteria.
     * @throws IllegalArgumentException if {@code formSearchRequest} is {@code null}.
     * @implNote This method interacts with the database to retrieve forms based on the search criteria provided in the {@code formSearchRequest}.
     *           It constructs a {@code BasicRestResponse} object with information about the status of the operation and the fetched form data.
     * @apiNote The returned {@code BasicRestResponse} includes a list of {@code FormResponseDto} objects representing the fetched forms.
     * @see FormSearchRequest
     * @see BasicRestResponse
     * @see FormResponseDto
     */
    @Override
    public BasicRestResponse fetchForms(final FormSearchRequest formSearchRequest) {
        // Initialize the response object
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        Specification<Form> spec = new Specification<Form>() {
            @Override
            public Predicate toPredicate(Root<Form> form, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                final List<Predicate> predicates = new ArrayList<>();

                // Filter by id
                if (formSearchRequest.getId() != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(form.get("id"), formSearchRequest.getId())));
                }

                // Filter by customerNam
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(formSearchRequest.getCustomerName())) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(form.get("customerName"), "%" + formSearchRequest.getCustomerName() + "%")));
                }

                // Filter by formName
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(formSearchRequest.getFormName())) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(form.get("formName"), "%" + formSearchRequest.getFormName() + "%")));
                }

                // Filter by submittedDate
                if (formSearchRequest.getSubmittedDate() != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(form.get("submittedDate"), formSearchRequest.getSubmittedDate())));
                }

                // Filter by formType
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(formSearchRequest.getFormType())) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(form.get("formType"), formSearchRequest.getFormType())));
                }

                // Filter by customerId
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(formSearchRequest.getCustomerId())) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(form.get("customerId"), formSearchRequest.getCustomerId())));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        try {
            // Create pageable object for pagination and sorting
            final Pageable pageable = PageRequest.of(formSearchRequest.getPageNumber(), formSearchRequest.getPageSize(), formSearchRequest.getSort());

            // Fetch forms from the database based on the search criteria
            final Page<Form> formList = formRepository.findAll(spec, pageable);

            // Map the fetched forms to DTOs
            final List<FormResponseDto> formResponseDtoList = formList.stream().map(form -> formMapper.mapToFormResponseDto(FormResponseDto.builder().build(), form)).collect(Collectors.toList());

            // Set response content and status
            response.setContent(formResponseDtoList);
            response.setMessage("Form list fetched successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            // Log and handle exceptions
            log.error("Error occurred while fetching all forms from the database", e);
            response.setMessage("Error occurred while fetching forms from the database: " + e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        // Return the response
        return response;
    }
}