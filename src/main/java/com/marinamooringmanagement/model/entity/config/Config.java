package com.marinamooringmanagement.model.entity.config;

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
@Table(name = "config")
public class Config extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Boolean isMarina;

    private Boolean isBoatyard;
}
