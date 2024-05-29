package com.marinamooringmanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "top_chain_condition")
public class TopChainCondition extends Base {

    @Column(name = "condition")
    private String condition;

    @Column(name = "description")
    private String description;

}
