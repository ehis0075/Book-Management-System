package com.bms.bms.author.service.impl;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.dto.AuthorListDTO;
import com.bms.bms.author.dto.AuthorRequestDTO;
import com.bms.bms.author.dto.CreateUpdateAuthorDTO;
import com.bms.bms.author.model.Author;
import com.bms.bms.author.repository.AuthorRepository;
import com.bms.bms.author.service.AuthorService;
import com.bms.bms.customSearch.CustomSearchService;
import com.bms.bms.customSearch.dto.AuthorSearchRequestDTO;
import com.bms.bms.exception.GeneralException;
import com.bms.bms.general.enums.ResponseCodeAndMessage;
import com.bms.bms.general.service.GeneralService;
import com.bms.bms.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final CustomSearchService customSearchService;

    private final GeneralService generalService;

    public AuthorServiceImpl(AuthorRepository authorRepository, CustomSearchService customSearchService, GeneralService generalService) {
        this.authorRepository = authorRepository;
        this.customSearchService = customSearchService;
        this.generalService = generalService;
    }


    @Override
    public AuthorDTO createAuthor(CreateUpdateAuthorDTO requestDto) {
        log.info("Request to create author with payload {}", requestDto);

        //check for null values
        if (GeneralUtil.stringIsNullOrEmpty(requestDto.getEmail())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Email cannot be null or empty!");
        }

        //check for null values
        if (GeneralUtil.stringIsNullOrEmpty(requestDto.getName())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Name cannot be null or empty!");
        }

        // validate email does not exist
        boolean isExist = authorRepository.existsByEmailAndName(requestDto.getEmail(), requestDto.getName());

        if (isExist) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Name and Email already exist");
        }

        Author author = new Author();
        author.setName(requestDto.getName());
        author.setEmail(requestDto.getEmail());

        author = authorRepository.save(author);

        return getAuthorDTO(author);
    }

    @Override
    public AuthorDTO updateAuthor(Long authorId, CreateUpdateAuthorDTO requestDto) {
        log.info("Request to update author with id {} and payload {}", authorId, requestDto);

        if (authorId == null) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Id cannot be null or empty!");
        }

        // validate email and does not exist
        boolean isExist = authorRepository.existsByEmailAndName(requestDto.getEmail(), requestDto.getName());

        if (isExist) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Name and Email already exist");
        }

        //get author from db
        Author author = getAuthorById(authorId);

        author.setName(requestDto.getName());
        author.setEmail(requestDto.getEmail());

        author = authorRepository.save(author);

        return getAuthorDTO(author);
    }

    @Override
    public void deleteAuthor(Long authorId) {
        log.info("Request to delete author with id {}", authorId);

        Author author = getAuthorById(authorId);

        authorRepository.delete(author);
    }

    @Override
    public AuthorListDTO getAuthorList(AuthorRequestDTO requestDTO) {
        log.info("Getting Author List");

        Pageable paged = generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage());
        Page<Author> superUserPage = authorRepository.findAll(paged);

        AuthorListDTO authorListDTO = new AuthorListDTO();

        List<Author> authorList = superUserPage.getContent();
        if (superUserPage.getContent().size() > 0) {
            authorListDTO.setHasNextRecord(superUserPage.hasNext());
            authorListDTO.setTotalCount((int) superUserPage.getTotalElements());
        }

        List<AuthorDTO> authorDTOS = convertToAuthorDTOList(authorList);
        authorListDTO.setAuthorDTOList(authorDTOS);

        return authorListDTO;
    }

    @Override
    public AuthorDTO getAuthorDTO(Author author) {
        log.info("Getting author DTO");

        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(authorDTO.getId());
        authorDTO.setName(author.getName());
        authorDTO.setEmail(author.getEmail());

        log.info("author dto {}", authorDTO);

        return authorDTO;
    }

    @Override
    public Author getAuthorById(Long authorId) {
        log.info("Request to get author with id {}", authorId);

        if (Objects.isNull(authorId)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author id cannot be empty!");
        }

        return authorRepository.findById(authorId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "Author does not exist"));
    }

    @Override
    public AuthorDTO getAuthorByAuthorName(String authorName) {
        log.info("Request to get author with name {}", authorName);

        if (GeneralUtil.stringIsNullOrEmpty(authorName)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Name cannot be empty!");
        }

        Author author = authorRepository.findByName(authorName);

        return getAuthorDTO(author);
    }

    @Override
    public AuthorListDTO searchAuthor(AuthorSearchRequestDTO requestDTO) {
        log.info("Searching Author List with {}", requestDTO);

        Page<Author> auditLogPage = customSearchService.searchAuthor(requestDTO);

        return getAuthorListDTO(auditLogPage);
    }

    private List<AuthorDTO> convertToAuthorDTOList(List<Author> authorList) {
        log.info("Converting Author List to Author DTO List");

        return authorList.stream().map(this::getAuthorDTO).collect(Collectors.toList());
    }

    public AuthorListDTO getAuthorListDTO(Page<Author> requestDTO) {
        log.info("Getting Author List");

        AuthorListDTO authorListDTO = new AuthorListDTO();

        List<Author> authorList = requestDTO.getContent();
        if (requestDTO.getContent().size() > 0) {
            authorListDTO.setHasNextRecord(requestDTO.hasNext());
            authorListDTO.setTotalCount((int) requestDTO.getTotalElements());
        }

        List<AuthorDTO> authorDTOS = convertToAuthorDTOList(authorList);
        authorListDTO.setAuthorDTOList(authorDTOS);

        return authorListDTO;
    }
}
