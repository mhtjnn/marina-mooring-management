package com.marinamooringmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eye_condition")
public class EyeCondition extends Base{

    @Column(name = "condition")
    private String condition;

    @Column(name = "description")
    private String description;

}
