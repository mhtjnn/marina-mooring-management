package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponseDto implements Serializable {

    private static final long serialVersionUID = 550266303507976L;

    private Integer id;

    private String name;

    private String label;
}
