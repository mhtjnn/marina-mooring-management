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
public class BoatyardMetadataResponse implements Serializable {

    private static final long serialVersionUID = 55268633455079L;

    private Integer id;

    private String boatyardName;

}
