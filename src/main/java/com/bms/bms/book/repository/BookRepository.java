package com.bms.bms.book.repository;

import com.bms.bms.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);

    Book findByTitle(String title);


}
