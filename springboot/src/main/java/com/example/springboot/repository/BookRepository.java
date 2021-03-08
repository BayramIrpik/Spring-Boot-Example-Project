package com.example.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	@Query("SELECT b FROM Book b WHERE b.deleted=0 and b.id = ?1")
	public Book getById(Long id);
	
	
	public List<Book> findByDeleted(boolean deleted);
	
}
