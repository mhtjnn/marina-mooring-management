package com.marinamooringmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a Role.
 * This class inherits common fields from the Base class and includes role-specific attributes.
 */
@Entity
@Table(name = "role")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends Base {

    private Integer id;

    /**
     * The name of the role.
     */
    @Column(name = "name")
    private String name;

    /**
     * The description of the role.
     */
    @Column(name = "description")
    private String description;

}

