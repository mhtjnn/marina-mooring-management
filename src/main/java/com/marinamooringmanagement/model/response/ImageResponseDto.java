package com.marinamooringmanagement.model.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDto {

    private Integer id;

    private String imageName;

    private String note;

    private byte[] imageData;
}
