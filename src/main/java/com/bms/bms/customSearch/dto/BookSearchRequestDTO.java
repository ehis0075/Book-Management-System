package com.bms.bms.customSearch.dto;

import com.bms.bms.general.dto.PageableRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BookSearchRequestDTO extends PageableRequestDTO {

    private String title;

    private String authorName;

}
