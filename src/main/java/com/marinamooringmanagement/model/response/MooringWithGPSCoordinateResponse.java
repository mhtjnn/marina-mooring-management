package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.MooringStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringWithGPSCoordinateResponse implements Serializable {

    private static final long serialVersionUID = 5526863675079L;

    private Integer id;

    private String mooringId;

    private String gpsCoordinates;

    private Integer statusId;

}
