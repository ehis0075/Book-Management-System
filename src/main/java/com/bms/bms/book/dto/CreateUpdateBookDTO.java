package com.bms.bms.book.dto;


import lombok.Data;

@Data
public class CreateUpdateBookDTO {

    private String title;

    private Long authorId;

    private String publicationYear;
}
