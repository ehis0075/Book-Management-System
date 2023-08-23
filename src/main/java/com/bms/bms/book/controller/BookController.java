package com.bms.bms.book.controller;


import com.bms.bms.book.dto.BookDTO;
import com.bms.bms.book.dto.BookListDTO;
import com.bms.bms.book.dto.BookRequestDTO;
import com.bms.bms.book.dto.CreateUpdateBookDTO;
import com.bms.bms.book.model.Book;
import com.bms.bms.book.service.BookService;
import com.bms.bms.customSearch.dto.BookSearchRequestDTO;
import com.bms.bms.general.dto.Response;
import com.bms.bms.general.enums.ResponseCodeAndMessage;
import com.bms.bms.general.service.GeneralService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    private final GeneralService generalService;

    public BookController(BookService bookService, GeneralService generalService) {
        this.bookService = bookService;
        this.generalService = generalService;
    }

    @PostMapping("/create")
    public Response createBook(@RequestBody CreateUpdateBookDTO requestDTO) {

        Book data = bookService.createBook(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/update/{bookId}")
    public Response createBook(@RequestBody CreateUpdateBookDTO requestDTO, @PathVariable Long bookId) {

        BookDTO data = bookService.updateBook(bookId, requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/delete/{bookId}")
    public Response deleteBook(@PathVariable Long bookId) {

        bookService.deleteBook(bookId);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, "");
    }

    @PostMapping("/getOne/{bookId}")
    public Response getBookById(@PathVariable Long bookId) {

        Book data = bookService.getBookById(bookId);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/getBookList/{authorId}")
    public Response getBookListForOneAuthor(@PathVariable Long authorId, @RequestBody BookRequestDTO requestDTO) {

        BookListDTO data = bookService.getBookListForOneAuthor(authorId, requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping()
    public Response getAll(@RequestBody BookRequestDTO requestDTO) {
        BookListDTO data = bookService.getBookList(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/search")
    public Response searchBook(@RequestBody BookSearchRequestDTO requestDTO) {
        BookListDTO data = bookService.searchBook(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }
}
