package com.marinamooringmanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * The Form class represents an entity for a form in the database.
 *
 * This class maps to a database table named "form" and contains fields that represent columns in the table.
 * It extends {@link Base}, inheriting common properties such as ID, created, and updated timestamps.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "form")
public class Form extends Base {

    /**
     * The unique identifier of the form.
     *
     * Mapped to the primary key column "id" in the "form" table.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the customer associated with the form.
     *
     * Mapped to the "customer_name" column in the "form" table.
     */
    @Column(name = "customer_name")
    private String customerName;

    /**
     * The name of the form.
     *
     * Mapped to the "form_name" column in the "form" table.
     */
    @Column(name = "form_name")
    private String formName;

    /**
     * The date when the form was submitted.
     *
     * Mapped to the "submitted_date" column in the "form" table.
     */
    @Column(name = "submitted_date")
    private Date submittedDate;

    /**
     * The MIME type of the form (e.g., "application/pdf" or "image/png").
     *
     * Mapped to the "form_type" column in the "form" table.
     */
    @Column(name = "form_type")
    private String formType;

    /**
     * The ID of the customer associated with the form.
     *
     * Mapped to the "customer_id" column in the "form" table.
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * The binary file data of the form.
     *
     * Mapped to the "file" column in the "form" table.
     * This column is defined as a BLOB in the database to accommodate large binary data.
     */
    @Column(name = "file", columnDefinition = "BLOB")
    @Lob
    private byte[] formFile;

    /**
     * The URL for downloading the form.
     *
     * Mapped to the "download_url" column in the "form" table.
     */
    @Column(name = "download_url")
    private String downloadUrl;
}
