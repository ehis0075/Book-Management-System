package com.bms.bms.author.service;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.dto.AuthorListDTO;
import com.bms.bms.author.dto.AuthorRequestDTO;
import com.bms.bms.author.dto.CreateUpdateAuthorDTO;
import com.bms.bms.author.model.Author;
import com.bms.bms.customSearch.dto.AuthorSearchRequestDTO;

public interface AuthorService {

    AuthorDTO createAuthor(CreateUpdateAuthorDTO requestDto);
    AuthorDTO updateAuthor(Long authorId, CreateUpdateAuthorDTO requestDto);
    void deleteAuthor(Long authorId);
    AuthorListDTO getAuthorList(AuthorRequestDTO requestDTO);

    AuthorDTO getAuthorDTO(Author author);

    Author getAuthorById(Long authorId);

    AuthorListDTO searchAuthor(AuthorSearchRequestDTO requestDTO);
}
