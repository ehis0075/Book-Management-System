package com.bms.bms.author.model;

import jakarta.persistence.*;
import lombok.*;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

}
