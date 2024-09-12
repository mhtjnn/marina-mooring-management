package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quickbook_customer")
public class QuickbookCustomer extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "quickbook_customer_first_name")
    private String quickbookCustomerFirstName;

    @Column(name = "quickbook_customer_last_name")
    private String quickbookCustomerLastName;

    @Column(name = "quickbook_customer_id")
    private String quickbookCustomerId;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    private Customer customer;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
