package com.bms.bms.book.service;

import com.bms.bms.book.dto.*;
import com.bms.bms.book.model.Book;
import com.bms.bms.customSearch.dto.BookSearchRequestDTO;

public interface BookService {

    Book createBook(CreateUpdateBookDTO requestDto);
    BookDTO updateBook(Long bookId, CreateUpdateBookDTO requestDto);
    void deleteBook(Long bookId);
    BookListDTO getBookList(BookRequestDTO requestDTO);

    BookListDTO getBookListForOneAuthor(Long authorId, BookRequestDTO requestDTO);

    BookDTO getBookDTO(Book book);

    Book getBookById(Long bookId);

    BookDTO getBookByBookTitle(String bookName);

    BookListDTO searchBook(BookSearchRequestDTO requestDTO);

}
