package com.marinamooringmanagement.model.entity.metadata;

import com.marinamooringmanagement.model.entity.Base;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eye_condition")
public class EyeCondition extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "_condition")
    private String condition;

    @Column(name = "description")
    private String description;

}
