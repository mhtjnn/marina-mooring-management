package com.marinamooringmanagement.model.dto.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobTypeDto {

    private Integer id;

    private String type;

    private String description;

}
