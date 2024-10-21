package com.marinamooringmanagement.model.entity.metadata;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.Base;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Entity class representing a country.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "country")
public class Country extends Base {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the country.
     */
    @Column(name = "country_name")
    private String name;

    /**
     * The label of the country.
     */
    @Column(name = "label")
    private String label;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "country", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<State> stateList;
}
