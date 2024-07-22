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
public class UserMetadataResponse implements Serializable {

    private static final long serialVersionUID = 552686350794654L;

    private Integer id;

    private String name;

}
