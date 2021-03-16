package com.example.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.Book;
import com.example.springboot.entity.User;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	@Query("SELECT b FROM Book b WHERE b.deleted=0 and b.id = ?1")
	public Book getById(Long id);
	
	public List<Book> findByDeleted(boolean deleted);
	
	public List<Book> findByDeletedAndReserve(boolean deleted,boolean reserve);
	
	public List<Book> findByDeletedAndTaken(boolean deleted,boolean taken);
	
	public List<Book> findByUsers(User user);
	
	
}
