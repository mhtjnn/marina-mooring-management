package com.marinamooringmanagement.api.v1.image;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.MultipleImageRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/image")
@CrossOrigin
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Operation(
            tags = "Image upload",
            description = "API to upload image",
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
    @PostMapping(value = "/upload/{entityId}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse addImage(
            final @PathVariable(value = "entityId") Integer entityId,
            final @RequestParam(value = "entity") String entity,
            final @RequestBody MultipleImageRequestDto multipleImageRequestDto,
            final HttpServletRequest request
    ) {

        return imageService.uploadImage(entityId, entity, multipleImageRequestDto, request);
    }

    @Operation(
            tags = "Fetch Images",
            description = "API to fetch images",
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
    @GetMapping(value = "/fetchImages/{entityId}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse fetchImages(
            final @PathVariable(value = "entityId") Integer entityId,
            final @RequestParam(value = "entity") String entity,
            final HttpServletRequest request
    ) {
        return imageService.fetchImages(entityId, entity, request);
    }

    @Operation(
            tags = "Edit image",
            description = "API to edit image",
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
    @PutMapping(value = "/editImage/{id}/{entityId}",
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER)
    public BasicRestResponse editImage(
            final @PathVariable(value = "id") Integer id,
            final @PathVariable(value = "entityId") Integer entityId,
            final @RequestParam(value = "entity") String entity,
            final @RequestBody ImageRequestDto imageRequestDto,
            final HttpServletRequest request
    ) {
        return imageService.editImage(id, entity, entityId, imageRequestDto ,request);
    }

}