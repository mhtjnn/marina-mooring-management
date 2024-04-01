package com.marinamooringmanagement.model.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO (Data Transfer Object) class for Base Entity.
 * This class serves as a base for other DTO classes.
 */
@MappedSuperclass
@Data
@ToString
@EqualsAndHashCode
public abstract class BaseDto implements Serializable {

    private static final long serialVersionUID = 2642629054722973598L;

    /**
     * The unique identifier for the entity.
     */
    private Integer id;

    /**
     * The creation date of the entity.
     */
    protected Date creationDate;

    /**
     * The user who created the entity.
     */
    protected String createdBy;

    /**
     * The last modification date of the entity.
     */
    private Date lastModifiedDate;

    /**
     * The user who last modified the entity.
     */
    protected String lastModifiedBy;
}


