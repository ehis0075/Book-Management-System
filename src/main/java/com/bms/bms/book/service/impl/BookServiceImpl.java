package com.bms.bms.book.service.impl;

import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.model.Author;
import com.bms.bms.author.service.AuthorService;
import com.bms.bms.book.dto.BookDTO;
import com.bms.bms.book.dto.BookListDTO;
import com.bms.bms.book.dto.BookRequestDTO;
import com.bms.bms.book.dto.CreateUpdateBookDTO;
import com.bms.bms.book.model.Book;
import com.bms.bms.book.repository.BookRepository;
import com.bms.bms.book.service.BookService;
import com.bms.bms.customSearch.CustomSearchService;
import com.bms.bms.customSearch.dto.BookSearchRequestDTO;
import com.bms.bms.exception.GeneralException;
import com.bms.bms.general.enums.ResponseCodeAndMessage;
import com.bms.bms.general.service.GeneralService;
import com.bms.bms.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final CustomSearchService customSearchService;

    private final AuthorService authorService;

    private final GeneralService generalService;

    public BookServiceImpl(BookRepository bookRepository, CustomSearchService customSearchService, AuthorService authorService, GeneralService generalService) {
        this.bookRepository = bookRepository;
        this.customSearchService = customSearchService;
        this.authorService = authorService;
        this.generalService = generalService;
    }


    @Override
    public Book createBook(CreateUpdateBookDTO requestDto) {
        log.info("Request to create book with payload {}", requestDto);

        if (GeneralUtil.stringIsNullOrEmpty(requestDto.getTitle())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book Title cannot be null or empty!");
        }

        if (Objects.isNull(requestDto.getAuthorId())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Id cannot be null or empty!");
        }

        if (Objects.isNull(requestDto.getPublicationYear())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Publication Year cannot be null or empty!");
        }

        // validate that name and title does not exit
        boolean isExist = bookRepository.existsByTitle(requestDto.getTitle());

        if (isExist) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Title already exist");
        }

        // get author from db
        Author author = authorService.getAuthorById(requestDto.getAuthorId());

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setPublicationYear(requestDto.getPublicationYear());
        book.setAuthor(author);

        book = bookRepository.save(book);

        return book;
    }

    @Override
    public BookDTO updateBook(Long bookId, CreateUpdateBookDTO requestDto) {
        log.info("Request to update book with payload {}", requestDto);

        if (Objects.isNull(bookId)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book Id cannot be null or empty!");
        }

        if (GeneralUtil.stringIsNullOrEmpty(requestDto.getTitle())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book Title cannot be null or empty!");
        }

        if (Objects.isNull(requestDto.getAuthorId())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Id cannot be null or empty!");
        }

        if (Objects.isNull(requestDto.getPublicationYear())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Publication Year cannot be null or empty!");
        }

        // validate that title does not exit
        boolean isExist = bookRepository.existsByTitle(requestDto.getTitle());

        if (isExist) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Title already exist");
        }

        // get author from db
        Author author = authorService.getAuthorById(requestDto.getAuthorId());

        // get book from db
        Book book = getBookById(bookId);

        book.setTitle(requestDto.getTitle());
        book.setPublicationYear(requestDto.getPublicationYear());
        book.setAuthor(author);

        book = bookRepository.save(book);

        return getBookDTO(book);
    }

    @Override
    public void deleteBook(Long bookId) {
        log.info("Request to delete book with id {}", bookId);

        Book book = getBookById(bookId);

        bookRepository.delete(book);
    }

    @Override
    public BookListDTO getBookList(BookRequestDTO requestDTO) {
        log.info("Getting Book List");

        Pageable paged = generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage());
        Page<Book> bookPage = bookRepository.findAll(paged);

        BookListDTO bookListDTO = new BookListDTO();

        List<Book> bookList = bookPage.getContent();
        if (bookPage.getContent().size() > 0) {
            bookListDTO.setHasNextRecord(bookPage.hasNext());
            bookListDTO.setTotalCount((int) bookPage.getTotalElements());
        }

        List<BookDTO> bookDTOS = convertToBookDTOList(bookList);
        bookListDTO.setBookDTOList(bookDTOS);

        return bookListDTO;
    }

    @Override
    public BookListDTO getBookListForOneAuthor(Long authorId, BookRequestDTO requestDTO) {
        log.info("Request to get Book List for author with Id {} and payload {}", authorId, requestDTO);

        if (Objects.isNull(authorId)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Author Id cannot be null or empty!");
        }

        boolean isExist = authorService.isExistById(authorId);

        if(!isExist){
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "Author does not exist!");

        }

        // get the page and size
        Pageable paged = generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage());

        Page<Book> bookPage = bookRepository.findAllByAuthor_Id(authorId, paged);

        BookListDTO bookListDTO = new BookListDTO();

        List<Book> bookList = bookPage.getContent();
        if (bookPage.getContent().size() > 0) {
            bookListDTO.setHasNextRecord(bookPage.hasNext());
            bookListDTO.setTotalCount((int) bookPage.getTotalElements());
        }

        // convert Book DTO to Book List
        List<BookDTO> bookDTOS = convertToBookDTOList(bookList);
        bookListDTO.setBookDTOList(bookDTOS);

        return bookListDTO;
    }

    @Override
    public BookDTO getBookDTO(Book book) {
        log.info("Getting Book DTO");

        AuthorDTO authorDTO = authorService.getAuthorDTO(book.getAuthor());

        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(bookDTO.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setPublicationYear(book.getPublicationYear());
        bookDTO.setAuthor(authorDTO);

        log.info("book dto {}", bookDTO);

        return bookDTO;
    }

    @Override
    public Book getBookById(Long bookId) {
        log.info("Request to get book with id {}", bookId);

        if (Objects.isNull(bookId)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book id cannot be empty!");
        }

        return bookRepository.findById(bookId).orElseThrow(() -> new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "Book does not exist"));
    }

    @Override
    public BookDTO getBookByBookTitle(String bookTitle) {
        log.info("Request to get book with name {}", bookTitle);

        if (GeneralUtil.stringIsNullOrEmpty(bookTitle)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book Name cannot be empty!");
        }

        Book book = bookRepository.findByTitle(bookTitle);

        if (Objects.isNull(book)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Book does noy exist!");
        }

        return getBookDTO(book);
    }

    @Override
    public BookListDTO searchBook(BookSearchRequestDTO requestDTO) {
        log.info("Searching Book List with {}", requestDTO);

        Page<Book> bookPage = customSearchService.searchBook(requestDTO);

        return getBookListDTO(bookPage);
    }

    private List<BookDTO> convertToBookDTOList(List<Book> bookList) {
        log.info("Converting Book List to Book DTO List");

        return bookList.stream().map(this::getBookDTO).collect(Collectors.toList());
    }

    public BookListDTO getBookListDTO(Page<Book> bookPage) {
        log.info("Getting Book List");


        BookListDTO bookListDTO = new BookListDTO();

        List<Book> authorList = bookPage.getContent();
        if (bookPage.getContent().size() > 0) {
            bookListDTO.setHasNextRecord(bookPage.hasNext());
            bookListDTO.setTotalCount((int) bookPage.getTotalElements());
        }

        List<BookDTO> bookDTOS = convertToBookDTOList(authorList);
        bookListDTO.setBookDTOList(bookDTOS);

        return bookListDTO;
    }
}
