package com.marinamooringmanagement.api.v1.voiceMEMO;

import com.marinamooringmanagement.constants.Authority;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.service.VoiceMEMOService;
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
@RequestMapping(value = "/api/v1/voiceMEMO")
@CrossOrigin
public class VoiceMEMOController {

    @Autowired
    private VoiceMEMOService voiceMEMOService;

    @Operation(
            summary = "API to get particular voice MEMO",
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
    @GetMapping(value = "/fetchVoiceMEMO/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize(Authority.ADMINISTRATOR + " or " + Authority.CUSTOMER_OWNER + " or " + Authority.TECHNICIAN)
    public BasicRestResponse fetchVoiceMEMO(
            final @PathVariable(value = "id") Integer id,
            final HttpServletRequest request
    ) {
        return voiceMEMOService.fetchVoiceMEMO(id, request);
    }

}
