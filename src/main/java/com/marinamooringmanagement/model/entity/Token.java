package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
/**
 * Entity class representing a Token.
 * This class inherits common fields from the Base class and includes token-specific attributes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token_config")
public class Token extends Base {

    /**
     * The token value.
     */
    @Column(name = "token")
    private String token;

    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * The expiration date and time of the token.
     */
    @Column(name = "expire_at")
    private Date tokenExpireAt;

    @Column(name = "refresh_token_expire_at")
    private Date refreshTokenExpireAt;

    /**
     * The user associated with the token.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
