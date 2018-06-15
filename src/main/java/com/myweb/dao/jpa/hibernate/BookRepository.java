package com.myweb.dao.jpa.hibernate;

import com.myweb.pojo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryDefinition(domainClass = Book.class, idClass = Integer.class)
public interface BookRepository extends JpaRepository<Book, Integer> {
}