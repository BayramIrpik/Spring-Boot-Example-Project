package com.example.springboot.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.entity.Book;
import com.example.springboot.service.BookService;

@RestController
@RequestMapping("/api/book")
public class BookRestController {

	@Autowired
	private BookService bookService;
	
	@GetMapping
	public List<Book> findAllBooks(){
		
		return bookService.getRestBookList();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Book> findUserById(@PathVariable(value = "id") long id) {
		Optional<Book> book = Optional.of(bookService.getByIdRest(id));

	    if(book.isPresent()) {
	        return ResponseEntity.ok().body(book.get());
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
}
