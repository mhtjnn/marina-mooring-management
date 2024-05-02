package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.Base;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateDto extends BaseDto {

    private static final long serialVersionUID = 55026806303507976L;

    private Integer id;

    private String name;

    private String label;
}