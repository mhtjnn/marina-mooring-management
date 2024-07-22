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
public class CustomerOwnerMetadataResponse implements Serializable {

    private static final long serialVersionUID = 55268635074669L;

    private Integer id;

    private String name;

}
