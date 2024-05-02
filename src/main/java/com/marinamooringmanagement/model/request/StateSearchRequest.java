package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateSearchRequest extends BaseSearchRequest{

    private Integer id;

    private String name;

    private String label;
}
