package com.marinamooringmanagement.model.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobLocationReportResponse {

    private Integer id;

    private String jobLocation;

    private double percentage;

}
