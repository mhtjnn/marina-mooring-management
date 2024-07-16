package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.entity.Mooring;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringsWithGPSCoordinatesResponse {

    private List<MooringResponseDto> mooringResponseDtoList;
    private List<MooringWithGPSCoordinateResponse> mooringWithGPSCoordinateResponseList;

}
