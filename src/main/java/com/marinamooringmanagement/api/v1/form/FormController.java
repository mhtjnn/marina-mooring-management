package com.marinamooringmanagement.api.v1.form;

import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.FormSearchRequest;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

/**
 * The FormController class handles requests related to forms in the application.
 *
 * It provides endpoints for uploading, downloading, and fetching forms from the database.
 * This controller leverages the FormService for business logic and data access.
 */
@RestController
@RequestMapping("api/v1/form")
@Validated
public class FormController {

    @Autowired
    private FormService formService;

    /**
     * Uploads a form to the database.
     * This API method allows uploading a form with associated customer information to the database.
     *
     * @param file         The file to be uploaded, wrapped as a {@link MultipartFile}.
     * @param customerName The name of the customer associated with the form.
     * @param customerId   The ID of the customer associated with the form.
     * @return A {@link BasicRestResponse} indicating the result of the operation.
     */
    @Operation(
            tags = "Upload form to the database",
            description = "API to Upload form to the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @PostMapping("/upload")
    public BasicRestResponse saveForm(
            @Parameter(description = "File to be uploaded", schema = @Schema(implementation = MultipartFile.class))
            final @NotNull @RequestPart("file") MultipartFile file,
            @Parameter(description = "Customer Name", schema = @Schema(implementation = String.class))
            final @NotNull @RequestPart("customerName") String customerName,
            @Parameter(description = "Customer ID", schema = @Schema(implementation = String.class))
            final @NotNull @RequestPart("customerId") String customerId
    ) {
        return formService.uploadForm(customerName, customerId, file);
    }

    /**
     * Downloads a form from the database.
     *
     * This API method allows downloading a form from the database based on the specified filename.
     *
     * @param fileName The name of the file to be downloaded.
     * @return A {@link ResponseEntity} containing a {@link ByteArrayResource} with the file content.
     */
    @Operation(
            tags = "Download file from the database",
            description = "API to Download file from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @GetMapping("/download/{filename}")
    public ResponseEntity<ByteArrayResource> downloadForm(
            @Parameter(description = "Name of the file to be downloaded", schema = @Schema(implementation = String.class))
            final @NotNull @PathVariable("filename") String fileName
    ) {
        return formService.downloadForm(fileName);
    }

    /**
     * Fetches all forms from the database with pagination and sorting options.
     *
     * This API method returns a paginated list of forms from the database, allowing customization with pagination
     * parameters, sorting, and direction.
     *
     * @param pageNumber The page number to fetch (default is 0).
     * @param pageSize   The number of forms per page (default is 10).
     * @param sortBy     The field to sort the forms by (default is "id").
     * @param sortDir    The sort direction, either "asc" for ascending or "desc" for descending (default is "asc").
     * @return A {@link BasicRestResponse} containing a paginated list of forms.
     */
    @Operation(
            tags = "Fetch all the forms from the database",
            description = "API to Fetch all the forms from the database",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @GetMapping("/")
    public BasicRestResponse fetchForms(
            @Parameter(description = "Page Number", schema = @Schema(implementation = Integer.class))
            final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            @Parameter(description = "Page Size", schema = @Schema(implementation = Integer.class))
            final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @Parameter(description = "Sort acc. to the field", schema = @Schema(implementation = String.class))
            final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @Parameter(description = "Sort direction (ascending or descending)", schema = @Schema(implementation = String.class))
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {

        FormSearchRequest formSearchRequest = FormSearchRequest.builder().build();
        formSearchRequest.setPageNumber(pageNumber);
        formSearchRequest.setPageSize(pageSize);
        formSearchRequest.setSort(new BaseSearchRequest().getSort(sortBy, sortDir));

        return formService.fetchForms(formSearchRequest);
    }
}
