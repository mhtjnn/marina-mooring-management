package com.marinamooringmanagement.model.entity.metadata;

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
@Table(name = "boat_type")
public class BoatType extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "boat_type")
    private String boatType;

    @Column(name = "description")
    private String description;
}
