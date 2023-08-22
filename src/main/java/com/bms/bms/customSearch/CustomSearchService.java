package com.bms.bms.customSearch;

import com.bms.bms.author.model.Author;
import com.bms.bms.customSearch.dto.AuthorSearchRequestDTO;
import org.springframework.data.domain.Page;

public interface CustomSearchService {
    Page<Author> searchAuthor(AuthorSearchRequestDTO searchMultipleDto);

}
