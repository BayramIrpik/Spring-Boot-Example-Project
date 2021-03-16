package com.example.springboot.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.BookDTO;
import com.example.springboot.service.BookService;

@RestController
@RequestMapping("/api/book")
public class BookRestController {

	@Autowired
	private BookService bookService;
	
	@GetMapping
	public List<BookDTO> findAllBooks(){
		
		return bookService.getRestBookList();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> findUserById(@PathVariable(value = "id") long id) {
		if(bookService.getByIdRest(id)==null) {
			 return ResponseEntity.status(HttpStatus.FORBIDDEN)
		                .body("There is no data for "+id+" id.");
		}
		Optional<BookDTO> bookDTO = Optional.of(bookService.getByIdRest(id));

	    if(bookDTO.isPresent()) {
	        return ResponseEntity.ok().body(bookDTO.get());
	    } else {
	       
	    	return ResponseEntity.notFound().build();
	    
	    }
	}
	
}
