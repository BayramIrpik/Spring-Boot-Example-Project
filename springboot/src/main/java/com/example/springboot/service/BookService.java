package com.example.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.springboot.entity.Book;
import com.example.springboot.entity.User;
import com.example.springboot.repository.BookRepository;

@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepository;
	
	public List<Book> getAllBooks(){
		
		return bookRepository.findByDeleted(false);
	}

	public Book getById(Long id) {
		
		return bookRepository.getById(id);
	}
	
	public void save(Book book) {
		
		bookRepository.save(book);
	}
	
	public List<Book> getReservedBooks(){
		
		return bookRepository.findByDeletedAndReserve(false, true);
	}
	
	public List<Book> getTakenBooks(){
		return bookRepository.findByDeletedAndTaken(false, true);
	}
	
	public List<Book> getBooksTakenByUser(User user){
		
		return bookRepository.findByUsers(user);
	}
}
