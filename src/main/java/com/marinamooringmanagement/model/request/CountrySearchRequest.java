package com.marinamooringmanagement.model.request;

import com.marinamooringmanagement.model.dto.RoleDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountrySearchRequest extends BaseSearchRequest{

    private Integer id;

    private String name;

    private String label;
}
