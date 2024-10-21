package com.marinamooringmanagement.model.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863507964L;

    private Integer id;

    private String imageName;

    private String note;

    private String encodedData;
}
