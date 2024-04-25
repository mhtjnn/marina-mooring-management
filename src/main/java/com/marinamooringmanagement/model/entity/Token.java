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

    /**
     * The expiration date and time of the token.
     */
    @Column(name = "expire_at")
    private Date expireAt;

    /**
     * The user associated with the token.
     */
    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
