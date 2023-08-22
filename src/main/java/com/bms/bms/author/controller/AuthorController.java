package com.bms.bms.author.controller;


import com.bms.bms.author.dto.AuthorDTO;
import com.bms.bms.author.dto.AuthorListDTO;
import com.bms.bms.author.dto.AuthorRequestDTO;
import com.bms.bms.author.dto.CreateUpdateAuthorDTO;
import com.bms.bms.author.service.AuthorService;
import com.bms.bms.customSearch.dto.AuthorSearchRequestDTO;
import com.bms.bms.general.dto.Response;
import com.bms.bms.general.enums.ResponseCodeAndMessage;
import com.bms.bms.general.service.GeneralService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final GeneralService generalService;

    public final AuthorService authorService;

    public AuthorController(GeneralService generalService, AuthorService authorService) {
        this.generalService = generalService;
        this.authorService = authorService;
    }

    @PostMapping("/create")
    public Response createAuthor(@RequestBody CreateUpdateAuthorDTO requestDTO) {

        AuthorDTO data = authorService.createAuthor(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/update/{authorId}")
    public Response createAuthor(@RequestBody CreateUpdateAuthorDTO requestDTO, @PathVariable Long authorId) {

        AuthorDTO data = authorService.updateAuthor(authorId, requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/delete/{authorId}")
    public Response deleteAuthor(@PathVariable Long authorId) {

        authorService.deleteAuthor(authorId);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, "");
    }

    @PostMapping()
    public Response getAll(@RequestBody AuthorRequestDTO requestDTO) {
        AuthorListDTO data = authorService.getAuthorList(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/search")
    public Response searchAudit(@RequestBody AuthorSearchRequestDTO requestDTO) {
        AuthorListDTO data = authorService.searchAuthor(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

}
