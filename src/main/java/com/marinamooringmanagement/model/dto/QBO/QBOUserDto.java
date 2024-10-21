package com.marinamooringmanagement.model.dto.QBO;

import com.marinamooringmanagement.model.dto.BaseDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QBOUserDto extends BaseDto {

    private Integer id;

    private String email;

    private String accessToken;

    private String refreshToken;

    private String realmId;

    private String authCode;

}
