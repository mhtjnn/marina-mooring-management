package com.marinamooringmanagement.model.dto.metadata;

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
public class MooringDueServiceStatusDto implements Serializable {

    private static final long serialVersionUID = 5502680630359L;

    private Integer id;

    private String status;

    private String description;
}
