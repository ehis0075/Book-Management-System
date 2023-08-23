package com.bms.bms.author.service;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.dto.AuthorListDTO;
import com.bms.bms.author.dto.AuthorRequestDTO;
import com.bms.bms.author.dto.CreateUpdateAuthorDTO;
import com.bms.bms.author.model.Author;
import com.bms.bms.author.repository.AuthorRepository;
import com.bms.bms.author.service.impl.AuthorServiceImpl;
import com.bms.bms.customSearch.CustomSearchService;
import com.bms.bms.exception.GeneralException;
import com.bms.bms.general.service.GeneralService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthorServiceTest {

    private GeneralService generalService;
    private AuthorRepository authorRepository;
    private CustomSearchService customSearchService;
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        generalService = mock(GeneralService.class);
        authorRepository = mock(AuthorRepository.class);
        customSearchService = mock(CustomSearchService.class);
        authorService = new AuthorServiceImpl(authorRepository, customSearchService, generalService);
    }

    @Test
    public void testCreateAuthor_whenValidData_shouldCreateAuthor() {

        // Arrange
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("Tope alabi");
        requestDto.setEmail("tope@example.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(new Author());

        // Act
        AuthorDTO authorDTO = authorService.createAuthor(requestDto);

        // Assert
        assertNotNull(authorDTO);
        assertEquals("Tope alabi", authorDTO.getName());
        assertEquals("tope@example.com", authorDTO.getEmail());
    }

    @Test
    public void testCreateAuthor_whenEmailExists_shouldThrowException() {
        // Arrange
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("ehis");
        requestDto.setEmail("ehisexample.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(true);

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.createAuthor(requestDto));
    }

    @Test
    public void testUpdateAuthor_whenValidData_shouldUpdateAuthor() {

        // Arrange
        Long authorId = 1L;
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("Oga");
        requestDto.setEmail("ogaboss@example.com");

        Author existingAuthor = new Author();
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);

        // Act
        AuthorDTO authorDTO = authorService.updateAuthor(authorId, requestDto);

        // Assert
        assertNotNull(authorDTO);
        assertEquals("Oga", authorDTO.getName());
        assertEquals("ogaboss@example.com", authorDTO.getEmail());
    }

    @Test
    public void testUpdateAuthor_whenAuthorDoesNotExist_shouldThrowException() {

        // Arrange
        Long authorId = 90L;
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("tade");
        requestDto.setEmail("tade@example.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.updateAuthor(authorId, requestDto));
    }

    @Test
    public void testDeleteAuthor_whenAuthorExists_shouldDeleteAuthor() {

        // Arrange
        Long authorId = 6L;
        Author existingAuthor = new Author();
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.of(existingAuthor));

        // Act
        authorService.deleteAuthor(authorId);

        // Assert
        verify(authorRepository, times(1)).delete(eq(existingAuthor));
    }

    @Test
    public void testDeleteAuthor_whenAuthorDoesNotExist_shouldThrowException() {

        // Arrange
        Long authorId = 3L;
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.deleteAuthor(authorId));
    }

    @Test
    public void testGetAuthorList_shouldReturnAuthorListDTO() {

        // Arrange
        AuthorRequestDTO requestDTO = new AuthorRequestDTO();
        requestDTO.setSize(10);
        requestDTO.setPage(0);

        List<Author> authorList = new ArrayList<>();
        Page<Author> authorPage = new PageImpl<>(authorList);

        Pageable pageable = mock(Pageable.class);
        when(generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage())).thenReturn(pageable);
        when(authorRepository.findAll(pageable)).thenReturn(authorPage);

        // Act
        AuthorListDTO authorListDTO = authorService.getAuthorList(requestDTO);

        // Assert
        assertNotNull(authorListDTO);
        assertEquals(authorList, authorListDTO.getAuthorDTOList());
    }

    @Test
    public void testGetAuthorDTO_shouldReturnAuthorDTO() {

        // Arrange
        Author author = new Author();
        author.setId(1L);
        author.setName("ehis");
        author.setEmail("ehis@gmail.com");

        // Act
        AuthorDTO authorDTO = authorService.getAuthorDTO(author);

        // Assert
        assertNotNull(authorDTO);
        assertEquals(author.getName(), authorDTO.getName());
        assertEquals(author.getEmail(), authorDTO.getEmail());
    }

    @Test
    public void testGetAuthorById_whenAuthorExists_shouldReturnAuthor() {
        // Arrange
        Long authorId = 1L;
        Author author = new Author();
        author.setId(authorId);

        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        // Act
        Author resultAuthor = authorService.getAuthorById(authorId);

        // Assert
        assertNotNull(resultAuthor);
        assertEquals(authorId, resultAuthor.getId());
    }

    @Test
    public void testGetAuthorById_whenAuthorDoesNotExist_shouldThrowException() {
        // Arrange
        Long authorId = 91L;

        when(authorRepository.findById(authorId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.getAuthorById(authorId));
    }

    @Test
    public void testGetAuthorByAuthorName_whenAuthorExists_shouldReturnAuthorDTO() {

        // Arrange
        String authorName = "ehis";
        Author author = new Author();
        author.setName(authorName);

        when(authorRepository.findByName(authorName)).thenReturn(author);

        // Act
        AuthorDTO authorDTO = authorService.getAuthorByAuthorName(authorName);

        // Assert
        assertNotNull(authorDTO);
        assertEquals(authorName, authorDTO.getName());
    }

    @Test
    public void testGetAuthorByAuthorName_whenAuthorDoesNotExist_shouldThrowException() { //

        // Arrange
        String authorName = "Nonexistent Author";

        when(authorRepository.findByName(authorName)).thenReturn(null);

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.getAuthorByAuthorName(authorName));
    }

}
