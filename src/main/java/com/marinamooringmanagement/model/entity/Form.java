package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "form_table")
public class Form extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "form_name")
    private String formName;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Column(name = "form_data", length = 102400)
    private byte[] formData;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_order_id")
    @JsonBackReference
    @ToString.Exclude
    private WorkOrder workOrder;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "parent_form_id")
    private Integer parentFormId;

    public Form(Integer id, String formName, String fileName, String createdBy, Timestamp createdTime,
                Integer userId, String firstName, String lastName, Integer roleId, String roleName) {
        this.id = id;
        this.formName = formName;
        this.fileName = fileName;
        this.createdBy = createdBy;
        this.creationDate = createdTime;
        this.user = User.builder().id(userId).firstName(firstName).lastName(lastName).role(Role.builder().id(roleId).name(roleName).build()).build();
    }

    public Form(Integer id, String formName, String fileName) {
        this.id = id;
        this.formName = formName;
        this.fileName = fileName;
    }

    public Form(Integer id, String formName, String fileName, Integer parentFormId) {
        this.id = id;
        this.formName = formName;
        this.fileName = fileName;
        this.parentFormId = parentFormId;
    }
}
