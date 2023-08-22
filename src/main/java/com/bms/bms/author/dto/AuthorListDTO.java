package com.bms.bms.author.dto;

import com.bms.bms.general.dto.PageableResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorListDTO extends PageableResponseDTO {

    @JsonProperty("authors")
    private List<AuthorDTO> authorDTOList;
}
