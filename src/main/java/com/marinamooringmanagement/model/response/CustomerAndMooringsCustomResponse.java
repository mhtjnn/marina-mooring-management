package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAndMooringsCustomResponse implements Serializable {

    private static final long serialVersionUID = 5526863534079L;

    CustomerResponseDto customerResponseDto;

    List<String> boatyardNames;
}
