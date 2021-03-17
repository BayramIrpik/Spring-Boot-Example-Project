package com.example.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot.detail.CustomUserDetails;
import com.example.springboot.entity.Book;
import com.example.springboot.entity.User;
import com.example.springboot.service.BookService;
import com.example.springboot.service.UserService;

@Controller
@RequestMapping("/user")
public class BookUserController {

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@RequestMapping("/reserve/{id}")
	public String reserve(@PathVariable(name = "id") Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userService.getById(userDetails.getId());

		Book book = bookService.getById(id);
		book.setUser(user);
		book.setReserve(true);
		bookService.save(book);

		return "redirect:/user/reserve";
	}

	@RequestMapping("/cancelRezervation/{id}")
	public String cancelRezervation(@PathVariable(name = "id") Long id) {
		Book book = bookService.getById(id);
		book.setUser(null);
		book.setReserve(false);
		bookService.save(book);

		return "redirect:/user/reserve";
	}

	@RequestMapping("/reserve")
	public String reserve(Model model) {

		List<Book> listBook = bookService.getAllBooks();
		model.addAttribute("listBook", listBook);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		model.addAttribute("userId", userDetails.getId().toString());
		return "reserve";
	}

	@RequestMapping("/borrowed")
	public String borrowed(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		User user = userService.getById(userDetails.getId());
		List<Book> listBook = bookService.getBooksTakenByUser(user);
		model.addAttribute("listBook", listBook);

		return "borrowed";
	}
}
