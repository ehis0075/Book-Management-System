package com.bms.bms.author.repository;

import com.bms.bms.author.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Boolean existsByEmailAndName(String email, String name);

    Author findByName(String name);


}
