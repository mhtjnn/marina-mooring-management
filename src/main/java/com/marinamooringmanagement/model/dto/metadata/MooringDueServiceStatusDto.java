package com.marinamooringmanagement.model.dto.metadata;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDueServiceStatusDto {

    private Integer id;

    private String status;

    private String description;
}
