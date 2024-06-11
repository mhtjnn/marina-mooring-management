package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDto implements Serializable {

    private static final long serialVersionUID = -3887461004331133L;

    private Integer id;

    /**
     * The name of the role.
     */
    private String name;

    private String description;
}
