package com.bms.bms.customSearch.dto;

import com.bms.bms.general.dto.PageableRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorSearchRequestDTO extends PageableRequestDTO {

    private String name;

    private String email;
}

