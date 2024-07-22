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
public class MooringMetadataResponse implements Serializable {

    private static final long serialVersionUID = 55268635079678L;

    private Integer id;

    private String mooringNumber;

}
