package com.bms.bms.book.dto;


import com.bms.bms.author.dto.AuthorDTO;
import lombok.Data;

@Data
public class BookDTO {

    private Long id;

    private String title;

    private AuthorDTO author;

    private String publicationYear;
}
