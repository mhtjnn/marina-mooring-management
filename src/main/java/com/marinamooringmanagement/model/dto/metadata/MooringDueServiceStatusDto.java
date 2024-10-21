package com.marinamooringmanagement.model.dto.metadata;

import com.marinamooringmanagement.model.dto.BaseDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MooringDueServiceStatusDto extends BaseDto {

    private Integer id;

    private String status;

    private String description;
}
