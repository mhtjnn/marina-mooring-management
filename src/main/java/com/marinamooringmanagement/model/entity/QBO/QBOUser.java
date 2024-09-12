package com.marinamooringmanagement.model.entity.QBO;

import com.marinamooringmanagement.model.entity.Base;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "qbo_user")
public class QBOUser extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Lob
    @Column(name = "access_token", length = 5024)
    private String accessToken;

    @Lob
    @Column(name = "refresh_token", length = 5024)
    private String refreshToken;

    @Column(name = "realmId")
    private String realmId;

    @Column(name = "auth_code")
    private String authCode;

}
