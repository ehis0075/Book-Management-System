package com.bms.bms.author.dto;

import jakarta.persistence.Column;
import lombok.Data;


@Data
public class CreateUpdateAuthorDTO {

    private String name;

    private String email;
}
