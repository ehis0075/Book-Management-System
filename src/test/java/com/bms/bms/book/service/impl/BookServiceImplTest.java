package com.bms.bms.book.service.impl;

import com.bms.bms.author.service.AuthorService;
import com.bms.bms.book.dto.BookDTO;
import com.bms.bms.book.dto.BookListDTO;
import com.bms.bms.book.dto.BookRequestDTO;
import com.bms.bms.book.dto.CreateUpdateBookDTO;
import com.bms.bms.book.model.Book;
import com.bms.bms.book.repository.BookRepository;
import com.bms.bms.customSearch.CustomSearchService;
import com.bms.bms.customSearch.dto.BookSearchRequestDTO;
import com.bms.bms.exception.GeneralException;
import com.bms.bms.general.enums.ResponseCodeAndMessage;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class BookServiceImplTest {

    private CustomSearchService customSearchService;
    private BookRepository bookRepository;
    private AuthorService authorService;
    private GeneralService generalService;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        customSearchService = mock(CustomSearchService.class);
        authorService = mock(AuthorService.class);
        generalService = mock(GeneralService.class);
        bookService = new BookServiceImpl(bookRepository, customSearchService, authorService, generalService);
    }

    @Test
    void createBook_whenValidData_shouldCreateBook() throws GeneralException { //
        // Arrange
        CreateUpdateBookDTO requestDto = new CreateUpdateBookDTO();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setAuthorId(1L);
        requestDto.setPublicationYear("1954");

        // Act
        Book book = bookService.createBook(requestDto);

        // Assert
        assertNotNull(book);
        assertEquals("The Lord of the Rings", book.getTitle());
        assertEquals(1L, book.getAuthor().getId());
        assertEquals("1954", book.getPublicationYear());
    }

    @Test
    void createBook_whenTitleIsNull_shouldThrowException() {
        // Arrange
        CreateUpdateBookDTO requestDto = new CreateUpdateBookDTO();
        requestDto.setAuthorId(1L);
        requestDto.setPublicationYear("1954");

        // Assert
        assertThrows(GeneralException.class, () -> bookService.createBook(requestDto));
    }

    @Test
    void createBook_whenAuthorIdIsNull_shouldThrowException() {
        // Arrange
        CreateUpdateBookDTO requestDto = new CreateUpdateBookDTO();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setPublicationYear("1954");

        // Act
        bookService = new BookServiceImpl(bookRepository, customSearchService, authorService, generalService);

        // Assert
        assertThrows(GeneralException.class, () -> bookService.createBook(requestDto));
    }

    @Test
    void createBook_whenPublicationYearIsNull_shouldThrowException() {
        // Arrange
        CreateUpdateBookDTO requestDto = new CreateUpdateBookDTO();
        requestDto.setTitle("The Lord of the Rings");
        requestDto.setAuthorId(1L);

        // Act
        bookService = new BookServiceImpl(bookRepository, customSearchService, authorService, generalService);

        // Assert
        assertThrows(GeneralException.class, () -> bookService.createBook(requestDto));
    }

    @Test
    public void testGetBookById_whenBookExists_shouldReturnBook() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act
        Book resultBook = bookService.getBookById(bookId);

        // Assert
        assertNotNull(resultBook);
        assertEquals(bookId, resultBook.getId());
    }

    @Test
    public void testGetBookById_whenBookDoesNotExist_shouldThrowException() {

        // Arrange
        Long bookId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(GeneralException.class, () -> bookService.getBookById(bookId));
    }

    @Test
    public void testGetBookByBookTitle_whenBookExists_shouldReturnBookDTO() {
        // Arrange
        String bookTitle = "Sample Book";
        Book book = new Book();
        book.setTitle(bookTitle);

        when(bookRepository.findByTitle(bookTitle)).thenReturn(book);

        // Act
        BookDTO bookDTO = bookService.getBookByBookTitle(bookTitle);

        // Assert
        assertNotNull(bookDTO);
        assertEquals(bookTitle, bookDTO.getTitle());
    }

    @Test
    public void testGetBookByBookTitle_whenBookDoesNotExist_shouldThrowException() {
        // Arrange
        String bookTitle = "Nonexistent Book";

        when(bookRepository.findByTitle(bookTitle)).thenReturn(null);

        // Act and Assert
        assertThrows(GeneralException.class, () -> bookService.getBookByBookTitle(bookTitle));
    }

    @Test
    public void testSearchBook_shouldReturnBookListDTO() {

        // Arrange
        BookSearchRequestDTO requestDTO = new BookSearchRequestDTO();
        List<Book> bookList = new ArrayList<>();
        Page<Book> bookPage = new PageImpl<>(bookList);

        when(customSearchService.searchBook(requestDTO)).thenReturn(bookPage);

        // Act
        BookListDTO bookListDTO = bookService.searchBook(requestDTO);

        // Assert
        assertNotNull(bookListDTO);
        assertEquals(bookList, bookListDTO.getBookDTOList());
    }

    @Test
    void testGetBookListForOneAuthor_whenValidAuthorIdAndRequestDTO_shouldReturnBookListDTO() {

        // Arrange
        Long authorId = 1L;
        BookRequestDTO requestDTO = new BookRequestDTO();
        requestDTO.setPage(0);
        requestDTO.setSize(10);

        List<Book> bookList = new ArrayList<>();
        bookList.add(new Book());
        Page<Book> bookPage = new PageImpl<>(bookList);

        when(authorService.isExistById(authorId)).thenReturn(true);
        when(generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage())).thenReturn(Pageable.unpaged());
        when(bookRepository.findAllByAuthor_Id(authorId, Pageable.unpaged())).thenReturn(bookPage);

        // Act
        BookListDTO result = bookService.getBookListForOneAuthor(authorId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(bookList.size(), result.getBookDTOList().size());
        assertFalse(result.isHasNextRecord());
        assertEquals(bookList.size(), result.getTotalCount());
    }

    @Test
    void testGetBookListForOneAuthor_whenAuthorIdNotFound_shouldThrowException() { //
        // Arrange
        Long authorId = 1L;
        BookRequestDTO requestDTO = new BookRequestDTO();

        when(authorService.isExistById(authorId)).thenReturn(false);

        // Act and Assert
        GeneralException exception = assertThrows(GeneralException.class, () -> bookService.getBookListForOneAuthor(authorId, requestDTO));
        assertEquals(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, exception.toString());
    }

}