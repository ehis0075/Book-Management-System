package com.bms.bms.book.model;

import com.bms.bms.author.model.Author;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Author> author;

    private String publicationYear;
}
