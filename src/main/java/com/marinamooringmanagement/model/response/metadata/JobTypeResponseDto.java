package com.marinamooringmanagement.model.response.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobTypeResponseDto implements Serializable {

    private static final long serialVersionUID = 552686374507968L;

    private Integer id;

    private String type;

    private String description;

}