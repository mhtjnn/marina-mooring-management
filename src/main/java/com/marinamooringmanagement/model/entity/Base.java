package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * Base class for entities.
 * This class provides common fields such as ID, creation date, created by, last modified date, and last modified by.
 */
@Data
@ToString
@EqualsAndHashCode
@MappedSuperclass
public class Base implements Serializable {

    /**
     * The unique identifier for the entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * The creation date of the entity.
     */
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdTime")
    protected Date creationDate;

    /**
     * The user who created the entity.
     */
    @Column(name = "createdBy")
    protected String createdBy;

    /**
     * The last modification date of the entity.
     */
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modifiedDate")
    protected Date lastModifiedDate;

    /**
     * The user who last modified the entity.
     */
    @Column(name = "lastModifiedBy")
    protected String lastModifiedBy;
}

