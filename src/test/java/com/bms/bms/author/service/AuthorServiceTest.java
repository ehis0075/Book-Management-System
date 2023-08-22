package com.bms.bms.author.service;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.dto.CreateUpdateAuthorDTO;
import com.bms.bms.author.model.Author;
import com.bms.bms.author.repository.AuthorRepository;
import com.bms.bms.exception.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testCreateAuthor_whenValidData_shouldCreateAuthor() {
        // Arrange
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("John Doe");
        requestDto.setEmail("johndoe@example.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(new Author());

        // Act
        AuthorDTO authorDTO = authorService.createAuthor(requestDto);

        // Assert
        assertNotNull(authorDTO);
        assertEquals("John Doe", authorDTO.getName());
        assertEquals("johndoe@example.com", authorDTO.getEmail());
    }

    @Test
    public void testCreateAuthor_whenEmailExists_shouldThrowException() {
        // Arrange
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("John Doe");
        requestDto.setEmail("johndoe@example.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(true);

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.createAuthor(requestDto));
    }

    @Test
    public void testUpdateAuthor_whenValidData_shouldUpdateAuthor() {
        // Arrange
        Long authorId = 1L;
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("janedoe@example.com");

        Author existingAuthor = new Author();
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);

        // Act
        AuthorDTO authorDTO = authorService.updateAuthor(authorId, requestDto);

        // Assert
        assertNotNull(authorDTO);
        assertEquals("Jane Doe", authorDTO.getName());
        assertEquals("janedoe@example.com", authorDTO.getEmail());
    }

    @Test
    public void testUpdateAuthor_whenAuthorDoesNotExist_shouldThrowException() {
        // Arrange
        Long authorId = 1L;
        CreateUpdateAuthorDTO requestDto = new CreateUpdateAuthorDTO();
        requestDto.setName("Jane Doe");
        requestDto.setEmail("janedoe@example.com");

        when(authorRepository.existsByEmailAndName(anyString(), anyString())).thenReturn(false);
        when(authorRepository.findById(eq(authorId))).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(GeneralException.class, () -> authorService.updateAuthor(authorId, requestDto));
    }

    @Test
    public void testDeleteAuthor_whenAuthorExists_shouldDeleteAuthor() {
        // Arrange
        Long authorId = 5L;
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

}
