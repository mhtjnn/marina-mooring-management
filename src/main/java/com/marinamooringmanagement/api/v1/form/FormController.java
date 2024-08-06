package com.marinamooringmanagement.api.v1.form;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.entity.Form;
import com.marinamooringmanagement.model.request.BaseSearchRequest;
import com.marinamooringmanagement.model.request.FormRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_NUM;
import static com.marinamooringmanagement.constants.AppConstants.DefaultPageConst.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping(value = "api/v1/form")
@CrossOrigin
@Validated
@Tag(name = "Form controller", description = "These are the API's for form upload, download and update.")
public class FormController extends GlobalExceptionHandler {

    @Autowired
    private FormService formService;

    @Operation(
            summary = "API to upload form",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PostMapping(value = "/uploadForm",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse uploadForm(
            final @RequestBody FormRequestDto formRequestDto,
            final HttpServletRequest request
    ) {

        return formService.uploadForm(formRequestDto, request);
    }

    @Operation(
            summary = "API to fetch forms",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/fetchForms",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchForms(
            final @RequestParam(value = "pageNumber", defaultValue = DEFAULT_PAGE_NUM, required = false) Integer pageNumber,
            final @RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            final @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            final @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            final @RequestParam(value = "searchText", required = false) String searchText,
            final HttpServletRequest request
    ) {
        final BaseSearchRequest baseSearchRequest = BaseSearchRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        return formService.fetchForms(baseSearchRequest, searchText, request);
    }

    @Operation(
            summary = "API to edit form",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @PutMapping(value = "/editForm/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse editForm(
            final @PathVariable(value = "id") Integer id,
            final @RequestBody FormRequestDto formRequestDto,
            final HttpServletRequest request
    ) {
        return formService.editForm(id, formRequestDto, request);
    }

    @Operation(
            summary = "API to delete form",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @DeleteMapping(value = "/deleteForm/{id}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse deleteForm(
            final @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        return formService.deleteForm(id, request);
    }

    @Operation(
            summary = "API to download form",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/downloadForm/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public ResponseEntity<ByteArrayResource> downloadForm(
            final @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        Form form = formService.downloadForm(id, request);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + form.getFileName() + "\"")
                .body(new ByteArrayResource(form.getFormData()));
    }

    @Operation(
            summary = "API to view form",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = {@Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json")},
                            responseCode = "400"
                    )
            }

    )
    @GetMapping(value = "/viewForm/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse viewForm(
            final @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        return formService.viewForm(id, request);
    }
}
