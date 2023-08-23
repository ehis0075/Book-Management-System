package com.bms.bms.customSearch.implementation;


import com.bms.bms.author.model.Author;
import com.bms.bms.book.model.Book;
import com.bms.bms.customSearch.CustomSearchService;
import com.bms.bms.customSearch.dto.AuthorSearchRequestDTO;
import com.bms.bms.customSearch.dto.BookSearchRequestDTO;
import com.bms.bms.util.GeneralUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class CustomSearchServiceImpl implements CustomSearchService {

    @PersistenceContext
    private EntityManager em;

    /**
     * This method searches the AuditLog table based on an array list of selected predicates, if its present it checks, else it doesn't
     */

    @Override
    public Page<Author> searchAuthor(AuthorSearchRequestDTO searchMultipleDto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Author> cq = cb.createQuery(Author.class);

        Root<Author> root = cq.from(Author.class);
        List<Predicate> predicates = new ArrayList<>();

        if (valid(searchMultipleDto.getEmail())) {
            predicates.add(cb.like(cb.lower(root.get("email")), '%' + searchMultipleDto.getEmail().toLowerCase(Locale.ROOT) + '%'));
        }

        if (valid(searchMultipleDto.getName())) {
            predicates.add(cb.like(cb.lower(root.get("name")), '%' + searchMultipleDto.getName().toLowerCase(Locale.ROOT) + '%'));
        }

        TypedQuery<?> query = em.createQuery(cq);


        return (Page<Author>) getPage(searchMultipleDto.getPage(), searchMultipleDto.getSize(), query);
    }

    @Override
    public Page<Book> searchBook(BookSearchRequestDTO searchMultipleDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);

        Root<Book> root = cq.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();

        if (valid(searchMultipleDto.getTitle())) {
            predicates.add(cb.like(cb.lower(root.get("title")), '%' + searchMultipleDto.getTitle().toLowerCase(Locale.ROOT) + '%'));
        }

        if (valid(searchMultipleDto.getAuthorName())) {
            predicates.add(cb.like(cb.lower(root.get("authorName")), '%' + searchMultipleDto.getAuthorName().toLowerCase(Locale.ROOT) + '%'));
        }

        TypedQuery<?> query = em.createQuery(cq);

        return (Page<Book>) getPage(searchMultipleDto.getPage(), searchMultipleDto.getSize(), query);
    }

    private PageImpl<?> getPage(int page, int size, TypedQuery<?> query) {
        Pageable paged;
        int totalRows;

        paged = PageRequest.of(page, size);
        totalRows = query.getResultList().size();

        query.setFirstResult(paged.getPageNumber() * paged.getPageSize());
        query.setMaxResults(paged.getPageSize());

        return new PageImpl<>(query.getResultList(), paged, totalRows);
    }

    private boolean valid(String value) {
        return !GeneralUtil.stringIsNullOrEmpty(value);
    }

}
