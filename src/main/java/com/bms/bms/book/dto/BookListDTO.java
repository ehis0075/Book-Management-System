package com.bms.bms.book.dto;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.general.dto.PageableResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class BookListDTO extends PageableResponseDTO {

    @JsonProperty("books")
    private List<BookDTO> bookDTOList;
}
