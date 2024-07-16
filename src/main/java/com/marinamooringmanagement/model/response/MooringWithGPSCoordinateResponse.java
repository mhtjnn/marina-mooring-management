package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.MooringStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringWithGPSCoordinateResponse {

    private Integer id;
    private String mooringId;
    private String gpsCoordinates;
    private Integer statusId;

}
