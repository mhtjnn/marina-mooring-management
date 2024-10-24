package com.marinamooringmanagement.model.entity.metadata;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.Base;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a state.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "state")
public class State extends Base {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the state.
     */
    @Column(name = "state_name")
    private String name;

    /**
     * The label of the state.
     */
    @Column(name = "label")
    private String label;

    @ManyToOne
    @JoinColumn(name = "country_id")
    @JsonBackReference
    private Country country;
}

