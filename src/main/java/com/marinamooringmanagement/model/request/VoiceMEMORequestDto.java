package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceMEMORequestDto implements Serializable {

    private static final long serialVersionUID = 5502683035509L;

    private Integer id;

    private String name;

    private String encodedData;
}
